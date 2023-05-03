package client;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import io.mosip.mock.sbi.R;
import io.mosip.mock.sbi.utility.utility.FileUtils;

/**
 * @author Anshul.Vanawat
 */

public class FileChooserFragment extends Fragment {
    public static final String LAST_UPLOAD_DATE = "lastUploadDate";
    private static final int MY_REQUEST_CODE_PERMISSION = 1000;
    private static final int MY_RESULT_CODE_FILE_CHOOSER = 2000;

    private TextView editTextPath;
    Uri selectedFileUri;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_file_chooser, container, false);

        editTextPath = rootView.findViewById(R.id.last_upload_date);
        assert getArguments() != null;
        String lastUploadedDate = getArguments().getString(LAST_UPLOAD_DATE);
        editTextPath.setText(lastUploadedDate);

        Button buttonBrowse = rootView.findViewById(R.id.button_browse);
        buttonBrowse.setOnClickListener(view -> askPermissionAndBrowseFile());
        return rootView;
    }

    private void askPermissionAndBrowseFile() {
        // Check if we have Call permission
        int permission = ActivityCompat.checkSelfPermission(this.getContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // If don't have permission so prompt the user.
            this.requestPermissions(
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_REQUEST_CODE_PERMISSION
            );
            return;
        }
        this.doBrowseFile();
    }

    private void doBrowseFile() {
        Intent chooseFileIntent = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFileIntent.setType("*/*");
        // Only return URIs that can be opened with ContentResolver
        chooseFileIntent.addCategory(Intent.CATEGORY_OPENABLE);

        chooseFileIntent = Intent.createChooser(chooseFileIntent, "Choose a file");
        startActivityForResult(chooseFileIntent, MY_RESULT_CODE_FILE_CHOOSER);
    }

    // When you have the request results
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case MY_REQUEST_CODE_PERMISSION: {
                // Note: If request is cancelled, the result arrays are empty.
                // Permissions granted (CALL_PHONE).
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    this.doBrowseFile();
                }
                break;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case MY_RESULT_CODE_FILE_CHOOSER:
                if (resultCode == Activity.RESULT_OK) {
                    if (data != null) {
                        selectedFileUri = data.getData();
                        Pair<String, String> fileNameAndSize = FileUtils.getFileNameAndSize(getContext(), selectedFileUri);
                        editTextPath.setText(String.format("%s (%s)", fileNameAndSize.first, fileNameAndSize.second));
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public Uri getSelectedUri() {
        return selectedFileUri;
    }

    public void resetSelection(String lastUploadDate) {
        selectedFileUri = null;
        if (editTextPath != null)
            editTextPath.setText(lastUploadDate);
    }
}