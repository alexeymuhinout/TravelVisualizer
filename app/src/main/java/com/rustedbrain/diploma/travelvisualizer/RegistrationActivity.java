package com.rustedbrain.diploma.travelvisualizer;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rustedbrain.diploma.travelvisualizer.model.dto.security.RegistrationRequest;
import com.rustedbrain.diploma.travelvisualizer.model.dto.security.Role;
import com.rustedbrain.diploma.travelvisualizer.model.dto.security.UserDTO;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class RegistrationActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, RegistrationFormKeyListener.SuccessListener {

    public static final String REG_MAIL_VALUE = "registration_mail";
    public static final String REG_PASSWORD_VALUE = "registration_pass";
    public static final String REG_USERNAME_VALUE = "registration_username";
    public static final String REG_FIRST_NAME_VALUE = "registration_first_name";
    public static final String REG_LAST_NAME_VALUE = "registration_last_name";
    public static final String REG_SUCCESS = "registration_success";

    private LinkedList<Integer> secretQueue = new LinkedList<>();

    private UserRegistrationTask mAuthTask = null;

    private AutoCompleteTextView emailEditText;
    private EditText usernameEditText;
    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText passwordEditText;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        emailEditText = findViewById(R.id.registration_email);
        emailEditText.setOnKeyListener(new RegistrationFormKeyListener(LoginActivity.SECRET_KEY_COMBINATION, secretQueue, this));

        passwordEditText = findViewById(R.id.registration_password);
        passwordEditText.setOnKeyListener(new RegistrationFormKeyListener(LoginActivity.SECRET_KEY_COMBINATION, secretQueue, this));
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptRegistration();
                    return true;
                }
                return false;
            }
        });

        usernameEditText = findViewById(R.id.registration_username);
        usernameEditText.setOnKeyListener(new RegistrationFormKeyListener(LoginActivity.SECRET_KEY_COMBINATION, secretQueue, this));

        firstNameEditText = findViewById(R.id.registration_firstname);
        firstNameEditText.setOnKeyListener(new RegistrationFormKeyListener(LoginActivity.SECRET_KEY_COMBINATION, secretQueue, this));

        lastNameEditText = findViewById(R.id.registration_lastname);
        lastNameEditText.setOnKeyListener(new RegistrationFormKeyListener(LoginActivity.SECRET_KEY_COMBINATION, secretQueue, this));

        Button registrationButton = findViewById(R.id.email_register_button);
        registrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegistration();
            }
        });

        mLoginFormView = findViewById(R.id.registration_form);
        mProgressView = findViewById(R.id.registration_progress);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            initViewValues(bundle);
        }
    }

    private void initViewValues(Bundle initBundle) {
        emailEditText.setText(initBundle.getString(REG_MAIL_VALUE));
        passwordEditText.setText(initBundle.getString(REG_PASSWORD_VALUE));
        usernameEditText.setText(initBundle.getString(REG_USERNAME_VALUE));
        firstNameEditText.setText(initBundle.getString(REG_FIRST_NAME_VALUE));
        lastNameEditText.setText(initBundle.getString(REG_LAST_NAME_VALUE));
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        initViewValues(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(REG_MAIL_VALUE, emailEditText.getText().toString());
        outState.putString(REG_PASSWORD_VALUE, passwordEditText.getText().toString());
        outState.putString(REG_USERNAME_VALUE, usernameEditText.getText().toString());
        outState.putString(REG_FIRST_NAME_VALUE, firstNameEditText.getText().toString());
        outState.putString(REG_LAST_NAME_VALUE, lastNameEditText.getText().toString());
        super.onSaveInstanceState(outState);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptRegistration() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        emailEditText.setError(null);
        passwordEditText.setError(null);
        usernameEditText.setError(null);
        firstNameEditText.setError(null);
        lastNameEditText.setError(null);

        // Store values at the time of the login attempt.
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String username = usernameEditText.getText().toString();
        String firstName = firstNameEditText.getText().toString();
        String lastName = lastNameEditText.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            passwordEditText.setError(getString(R.string.error_invalid_password));
            focusView = passwordEditText;
            cancel = true;
        }

        if (TextUtils.isEmpty(username)) {
            usernameEditText.setError(getString(R.string.error_empty_username));
            focusView = usernameEditText;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            emailEditText.setError(getString(R.string.error_field_required));
            focusView = emailEditText;
            cancel = true;
        } else if (!isEmailValid(email)) {
            emailEditText.setError(getString(R.string.error_invalid_email));
            focusView = emailEditText;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserRegistrationTask(email, password, username, firstName, lastName, Role.USER);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), RegistrationActivity.ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(RegistrationActivity.ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(RegistrationActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        emailEditText.setAdapter(adapter);
    }

    @Override
    public void combinationSuccess() {
        usernameEditText.setText("admin");
        firstNameEditText.setText("admin");
        lastNameEditText.setText("admin");
        emailEditText.setText("admin@gmail.com");
        passwordEditText.setText("admin");
        Toast.makeText(getApplicationContext(), "Secret combination inputted successfully!", Toast.LENGTH_SHORT).show();
    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }


    private class UserRegistrationTask extends AsyncTask<Void, Void, UserDTO> {

        private final String email;
        private final String password;
        private final String username;
        private final String firstName;
        private final String lastName;
        private final Role role;

        UserRegistrationTask(String email, String password, String username, String firstName, String lastName, Role role) {
            this.email = email;
            this.password = password;
            this.username = username;
            this.firstName = firstName;
            this.lastName = lastName;
            this.role = role;
        }

        @Override
        protected UserDTO doInBackground(Void... params) {
            try {
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

                RegistrationRequest registrationRequest = new RegistrationRequest();
                registrationRequest.setMail(email);
                registrationRequest.setPassword(password);
                registrationRequest.setUsername(username);
                registrationRequest.setFirstName(firstName);
                registrationRequest.setLastName(lastName);
                registrationRequest.setRole(role);

                return restTemplate.postForObject(new URI(TravelAppUtils.getAbsoluteUrl(TravelAppUtils.REGISTER_URL)),
                        registrationRequest, UserDTO.class);
            } catch (HttpClientErrorException e) {
                Log.e("MainActivity", e.getMessage(), e);
                try {
                    return new ObjectMapper().readValue(e.getResponseBodyAsString(), UserDTO.class);
                } catch (IOException e1) {
                    return null;
                }
            } catch (URISyntaxException e) {
                Log.e("MainActivity", e.getMessage(), e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(final UserDTO responseEntity) {
            mAuthTask = null;
            showProgress(false);

            if (responseEntity == null) {
                Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.error_invalid_url), Toast.LENGTH_LONG);
                toast.show();
            } else if (HttpStatus.OK.equals(responseEntity.getStatus())) {
                Intent loginIntent = new Intent(RegistrationActivity.this.getApplicationContext(), LoginActivity.class);

                Bundle extras = new Bundle();
                extras.putString(REG_MAIL_VALUE, password);
                extras.putBoolean(REG_SUCCESS, true);

                loginIntent.putExtras(extras);
                startActivity(loginIntent);
            } else if (HttpStatus.CONFLICT.equals(responseEntity.getStatus())) {
                Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.error_user_registered), Toast.LENGTH_LONG);
                toast.show();
            } else {
                passwordEditText.setError(getString(R.string.error_incorrect_password));
                passwordEditText.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}
