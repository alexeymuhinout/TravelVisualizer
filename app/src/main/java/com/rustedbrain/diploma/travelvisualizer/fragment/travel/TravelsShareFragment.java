package com.rustedbrain.diploma.travelvisualizer.fragment.travel;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.WriterException;
import com.rustedbrain.diploma.travelvisualizer.LoginActivity;
import com.rustedbrain.diploma.travelvisualizer.R;
import com.rustedbrain.diploma.travelvisualizer.model.dto.security.AuthUserDTO;
import com.rustedbrain.diploma.travelvisualizer.model.dto.security.UserDTO;
import com.rustedbrain.diploma.travelvisualizer.model.dto.security.UserDTOList;
import com.rustedbrain.diploma.travelvisualizer.model.dto.travel.TravelDTO;
import com.rustedbrain.diploma.travelvisualizer.task.authentication.GetUsersTask;
import com.rustedbrain.diploma.travelvisualizer.task.travel.SetSharedUsersTask;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import androidmads.library.qrgenearator.QRGSaver;

import static android.content.Context.WINDOW_SERVICE;

public class TravelsShareFragment extends Fragment implements GetUsersTask.Listener, SetSharedUsersTask.Listener {

    private static final String TRAVEL_NAME_PARAM = "travel_name";
    private static final String TAG = "GenerateQRCode";
    private static final String USER_NAME_PARAM = "user_name";
    private static final String USERS_NAMES_PARAM = "users_names";
    String savePath = Environment.getExternalStorageDirectory().getPath() + "/QRCode/";

    private String travelName;
    private TravelsShareFragmentListener mListener;
    private QRGEncoder qrgEncoder;
    private Bitmap bitmap;
    private ImageView qrImage;
    private String userName;
    private AutoCompleteTextView usernamesAutocompleteTextField;
    private List<String> usernames;
    private GetUsersTask getUsersTask;
    private Button buttonSharedUserAdd;
    private Button buttonCancel;
    private Button buttonSubmit;
    private SetSharedUsersTask setSharedUsersTask;


    private LinearLayout sharedUsersLayout;
    private Map<String, SharedUserLayout> sharedUserLayoutMap = new HashMap<>();
    private AuthUserDTO user;

    public TravelsShareFragment() {
    }

    public static TravelsShareFragment newInstance(AuthUserDTO user, String travelName) {
        TravelsShareFragment fragment = new TravelsShareFragment();
        Bundle args = new Bundle();
        args.putSerializable(LoginActivity.USER_DTO_PARAM, user);
        args.putString(TRAVEL_NAME_PARAM, travelName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            travelName = getArguments().getString(TRAVEL_NAME_PARAM);
            user = (AuthUserDTO) getArguments().getSerializable(LoginActivity.USER_DTO_PARAM);
            usernames = getArguments().getStringArrayList(USERS_NAMES_PARAM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_travels_share, container, false);

        buttonCancel = view.findViewById(R.id.travel_share_button_cancel);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonCancelClicked();
            }
        });

        buttonSubmit = view.findViewById(R.id.travel_share_button_submit);
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonSubmitClicked();
            }
        });

        qrImage = view.findViewById(R.id.QR_Image);
        generateQR(userName, travelName);

        buttonSharedUserAdd = view.findViewById(R.id.button_shared_user_add);
        buttonSharedUserAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonSharedUserAddClicked();
            }
        });

        sharedUsersLayout = view.findViewById(R.id.travel_share_usernames_layout);

        usernamesAutocompleteTextField = view.findViewById(R.id.travel_share_username_autocomplete);
        retrieveUsernames();

        return view;
    }

    private void onButtonSubmitClicked() {
        Collection<String> usernames = sharedUserLayoutMap.keySet();

        if (mListener != null && !usernames.isEmpty()) {
            if (setSharedUsersTask != null) {
                return;
            }

            showSetSharedUsersTaskProgress(true);
            this.setSharedUsersTask = new SetSharedUsersTask(user, user.getUsername(), travelName, usernames, this);
            this.setSharedUsersTask.execute((Void) null);
        }
    }

    private void onButtonCancelClicked() {
        if (mListener != null) {
            mListener.onTravelShareFragmentCancelButtonClicked();
        }
    }

    private void onButtonSharedUserAddClicked() {
        String username = usernamesAutocompleteTextField.getText().toString();
        if (user.getUsername().equals(username)) {
            Toast toast = Toast.makeText(getContext(), "Unable to add yourself", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } else if (sharedUserLayoutMap.containsKey(username)) {
            Toast toast = Toast.makeText(getContext(), "User already added", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } else {
            SharedUserLayout layout = new SharedUserLayout(getContext(), username);
            sharedUsersLayout.addView(layout);
            sharedUserLayoutMap.put(username, layout);
        }
    }

    private void save(String qrEncodeValue) {
        boolean save;
        String result;
        try {
            save = QRGSaver.save(savePath, qrEncodeValue, bitmap, QRGContents.ImageType.IMAGE_JPEG);
            result = save ? "Image Saved" : "Image Not Saved";
            Toast.makeText(getContext(), result, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void retrieveUsernames() {
        if (mListener != null) {
            if (getUsersTask != null) {
                return;
            }

            this.getUsersTask = new GetUsersTask(this);
            this.getUsersTask.execute((Void) null);
        }
    }

    private void generateQR(String username, String travelName) {
        WindowManager manager = (WindowManager) getActivity().getSystemService(WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        int width = point.x;
        int height = point.y;
        int smallerDimension = width < height ? width : height;
        smallerDimension = smallerDimension * 3 / 4;

        qrgEncoder = new QRGEncoder(
                username + "|" + travelName, null,
                QRGContents.Type.TEXT,
                smallerDimension);
        try {
            bitmap = qrgEncoder.encodeAsBitmap();
            qrImage.setImageBitmap(bitmap);
        } catch (WriterException e) {
            Log.v(TAG, e.toString());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof TravelsShareFragmentListener) {
            mListener = (TravelsShareFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void setGetUsersTask(GetUsersTask getUsersTask) {
        this.getUsersTask = getUsersTask;
    }

    @Override
    public void showGetUsersTaskProgress(boolean show) {

    }

    @Override
    public void showUserLoginTaskUnknownError() {

    }

    @Override
    public void fireUsersRetrieved(UserDTOList userDTOList) {
        if (userDTOList == null || userDTOList.getUserDTOList().isEmpty()) {
            this.usernames = Collections.emptyList();
        } else {
            this.usernames = new ArrayList<>(userDTOList.getUserDTOList().size());
            for (UserDTO userDTO : userDTOList.getUserDTOList()) {
                this.usernames.add(userDTO.getUsername());
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this.getContext(),
                android.R.layout.simple_dropdown_item_1line, usernames);
        usernamesAutocompleteTextField.setAdapter(adapter);
    }

    @Override
    public void setSetSharedUsersTask(SetSharedUsersTask setSharedUsersTask) {
        this.setSharedUsersTask = setSharedUsersTask;
    }

    @Override
    public void showSetSharedUsersTaskProgress(boolean show) {

    }

    @Override
    public void showSetSharedUsersTaskError() {
        Toast.makeText(getContext(), "Error occurred during setting shared users", Toast.LENGTH_LONG).show();
    }

    @Override
    public void showTravel(TravelDTO travelDTO) {
        if (mListener != null) {
            mListener.onTravelShareFragmentUsersShared(travelDTO);
        }
    }

    public interface TravelsShareFragmentListener {
        void onTravelShareFragmentCancelButtonClicked();

        void onTravelShareFragmentUsersShared(TravelDTO travelDTO);
    }

    private class SharedUserLayout extends LinearLayout {

        private final String username;

        public SharedUserLayout(Context context, final String username) {
            super(context);
            this.username = username;

            TextView usernameTextView = new TextView(getContext());
            LinearLayout.LayoutParams authorTextViewLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            authorTextViewLayoutParams.setMargins(5, 5, 5, 5);
            usernameTextView.setLayoutParams(authorTextViewLayoutParams);
            usernameTextView.setText(username);

            final LinearLayout sharedUserControlsLayout = new LinearLayout(getContext());
            sharedUserControlsLayout.setOrientation(LinearLayout.HORIZONTAL);
            ImageButton deleteSharedUserButton = new ImageButton(getContext());
            deleteSharedUserButton.setImageResource(R.drawable.ic_map_travel_trash);
            deleteSharedUserButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TravelsShareFragment.this.sharedUsersLayout.removeView(SharedUserLayout.this);
                    TravelsShareFragment.this.sharedUserLayoutMap.remove(username);
                }
            });
            sharedUserControlsLayout.addView(deleteSharedUserButton);

            this.setOrientation(LinearLayout.HORIZONTAL);
            this.setBackgroundColor(Color.parseColor("#ffffff"));
            this.addView(usernameTextView);
            this.addView(sharedUserControlsLayout);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
            layoutParams.setMargins(5, 5, 5, 5);
            this.setLayoutParams(layoutParams);
        }

        public String getUsername() {
            return username;
        }
    }
}
