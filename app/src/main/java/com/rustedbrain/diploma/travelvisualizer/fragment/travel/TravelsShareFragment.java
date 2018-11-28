package com.rustedbrain.diploma.travelvisualizer.fragment.travel;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.WriterException;
import com.rustedbrain.diploma.travelvisualizer.R;
import com.rustedbrain.diploma.travelvisualizer.model.dto.security.UserDTO;
import com.rustedbrain.diploma.travelvisualizer.model.dto.security.UserDTOList;
import com.rustedbrain.diploma.travelvisualizer.task.authentication.GetUsersTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import androidmads.library.qrgenearator.QRGSaver;

import static android.content.Context.WINDOW_SERVICE;

public class TravelsShareFragment extends Fragment implements GetUsersTask.Listener {

    private static final String TRAVEL_NAME_PARAM = "travel_name";
    private static final String TAG = "GenerateQRCode";
    private static final String USER_NAME_PARAM = "user_name";
    private static final String USERS_NAMES_PARAM = "users_names";
    String savePath = Environment.getExternalStorageDirectory().getPath() + "/QRCode/";
    // TODO: Rename and change types of parameters
    private String travelName;
    private OnFragmentInteractionListener mListener;
    private QRGEncoder qrgEncoder;
    private Bitmap bitmap;
    private ImageView qrImage;
    private String userName;
    private AutoCompleteTextView usernamesAutocompleteTextField;
    private List<String> usernames;
    private GetUsersTask getUsersTask;
    private Button buttonSharedUserAdd;

    public TravelsShareFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param qrEncodeValue Parameter 1.
     * @return A new instance of fragment TravelsShareFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TravelsShareFragment newInstance(String username, String qrEncodeValue) {
        TravelsShareFragment fragment = new TravelsShareFragment();
        Bundle args = new Bundle();
        args.putString(USER_NAME_PARAM, username);
        args.putString(TRAVEL_NAME_PARAM, qrEncodeValue);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            travelName = getArguments().getString(TRAVEL_NAME_PARAM);
            userName = getArguments().getString(USER_NAME_PARAM);
            usernames = getArguments().getStringArrayList(USERS_NAMES_PARAM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_travels_share, container, false);

        qrImage = view.findViewById(R.id.QR_Image);
        generateQR(userName, travelName);

        buttonSharedUserAdd = view.findViewById(R.id.button_shared_user_add);
        buttonSharedUserAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonSharedUserAddClicked();
            }
        });

        usernamesAutocompleteTextField = view.findViewById(R.id.travel_share_username_autocomplete);
        retrieveUsernames();

        return view;
    }

    private void onButtonSharedUserAddClicked() {
        String username = usernamesAutocompleteTextField.getText().toString();
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

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
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
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getContext(),
                android.R.layout.simple_dropdown_item_1line, usernames);
        usernamesAutocompleteTextField.setAdapter(adapter);
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
