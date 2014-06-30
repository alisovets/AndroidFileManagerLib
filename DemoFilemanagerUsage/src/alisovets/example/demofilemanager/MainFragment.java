package alisovets.example.demofilemanager;

import alisovets.lib.uilib.dialog.MessageDialogFragment;
import alisovets.lib.uilib.file.FileManagerActivity;
import alisovets.lib.uilib.file.FileManagerFragment;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * 
 * It is demonstrates few examples of usage FileManagerActivity from UILib library    
 * 
 * @author Alexander Lisovets, 2014
 *
 */
public class MainFragment extends Fragment implements OnClickListener {

	private final static int OPEN_FILE_REQUEST_CODE = 1;
	private final static int SAVE_FILE_REQUEST_CODE = 2;
	private final static String MESSAGE_DIALOG_TAG = "message_dlg";

	private Context mContext;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_main, container, false);
		mContext = getActivity();

		Button button = (Button) rootView.findViewById(R.id.default_filemanager_btn);
		button.setOnClickListener(this);

		button = (Button) rootView.findViewById(R.id.open_file_btn);
		button.setOnClickListener(this);

		button = (Button) rootView.findViewById(R.id.open_file_no_menu_btn);
		button.setOnClickListener(this);

		button = (Button) rootView.findViewById(R.id.open_text_file_btn);
		button.setOnClickListener(this);

		button = (Button) rootView.findViewById(R.id.save_file_btn);
		button.setOnClickListener(this);

		setHasOptionsMenu(true);
		return rootView;
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.default_filemanager_btn:
			clickDefault();
			break;

		case R.id.open_file_btn:
			clickOpenFile();
			break;
		case R.id.open_file_no_menu_btn:
			clickOpenFileNoMenu();
			break;
		case R.id.open_text_file_btn:
			clickOpenTextZipFile();
			break;
		case R.id.save_file_btn:
			clickSaveTextFile();
			break;

		default:
			break;
		}

	}

	/*
	 * Opens FileManagerActivity with default parameters.
	 * It is works as a full-featured file manager. 
	 * 
	 */
	private void clickDefault() {
		Intent intent = new Intent(mContext, FileManagerActivity.class);
		startActivity(intent);
	}

	/*
	 * Opens FileManagerActivity to select any file with any extension to open it.
	 * The popup menu has all appropriate items.
	 */
	private void clickOpenFile() {

		final String regex = ".+\\.*";
		final int iconId = R.drawable.ic_launcher;

		String startPath = Environment.getExternalStorageDirectory().getAbsolutePath();
		String rootPath = startPath;

		Intent intent = new Intent(mContext, FileManagerActivity.class);
		intent.putExtra(FileManagerFragment.ROOT_PATH_KEY, rootPath);
		intent.putExtra(FileManagerFragment.START_PATH_KEY, startPath);
		intent.putExtra(FileManagerFragment.FILENAME_REGEX_KEY, regex);
		intent.putExtra(FileManagerFragment.ICONE_ID_KEY, iconId);
		intent.putExtra(FileManagerFragment.INIT_FILENAME_KEY, "");
		int actionFlags = FileManagerFragment.OPEN_ACTION_FLAG;
		intent.putExtra(FileManagerFragment.ACTION_FLAGS_KEY, actionFlags);

		startActivityForResult(intent, OPEN_FILE_REQUEST_CODE);

	}

	/*
	 * Opens FileManagerActivity to select any file with any extension to open it.
	 * The popup menu has no items.
	 */
	private void clickOpenFileNoMenu() {

		final String regex = ".+\\.*";
		final int iconId = R.drawable.ic_launcher;

		String startPath = Environment.getExternalStorageDirectory().getAbsolutePath();
		String rootPath = startPath;

		Intent intent = new Intent(getActivity(), FileManagerActivity.class);
		intent.putExtra(FileManagerActivity.ACTIVITY_TITLE_KEY, mContext.getString(R.string.open_file_no_menu_title));
		intent.putExtra(FileManagerFragment.ROOT_PATH_KEY, rootPath);
		intent.putExtra(FileManagerFragment.START_PATH_KEY, startPath);
		intent.putExtra(FileManagerFragment.FILENAME_REGEX_KEY, regex);
		intent.putExtra(FileManagerFragment.ICONE_ID_KEY, iconId);
		intent.putExtra(FileManagerFragment.INIT_FILENAME_KEY, "");
		int actionFlags = FileManagerFragment.OPEN_ACTION_FLAG;
		intent.putExtra(FileManagerFragment.ACTION_FLAGS_KEY, actionFlags);
		intent.putExtra(FileManagerFragment.MENU_FLAGS_KEY, 0);
		startActivityForResult(intent, OPEN_FILE_REQUEST_CODE);
	}

	
	/*
	 * Opens FileManagerActivity to select a file with any '.zip' or '.txt' extension to open it.
	 * The popup menu has all appropriate items.
	 */
	private void clickOpenTextZipFile() {

		final String regex = "(.+\\.zip)|(.+\\.txt)";
		final int iconId = R.drawable.ic_launcher;

		String startPath = Environment.getExternalStorageDirectory().getAbsolutePath();
		String rootPath = startPath;

		Intent intent = new Intent(mContext, FileManagerActivity.class);
		intent.putExtra(FileManagerFragment.ROOT_PATH_KEY, rootPath);
		intent.putExtra(FileManagerFragment.START_PATH_KEY, startPath);
		intent.putExtra(FileManagerFragment.FILENAME_REGEX_KEY, regex);
		intent.putExtra(FileManagerFragment.ICONE_ID_KEY, iconId);

		intent.putExtra(FileManagerFragment.INIT_FILENAME_KEY, "");
		int actionFlags = FileManagerFragment.OPEN_ACTION_FLAG;
		intent.putExtra(FileManagerFragment.ACTION_FLAGS_KEY, actionFlags);
		startActivityForResult(intent, OPEN_FILE_REQUEST_CODE);

	}

	/*
	 * Opens FileManagerActivity to select a txt file to save it.
	 * This allows you to select a directory and file and enter a file name from the keyboard.
	 * The popup menu has 3 items 'Create Folder', 'Rename', 'Details' .
	 */
	private void clickSaveTextFile() {

		final String regex = "(.+\\.txt)";
		final int iconId = R.drawable.ic_launcher;
		String startPath = Environment.getExternalStorageDirectory().getAbsolutePath();
		Intent intent = new Intent(mContext, FileManagerActivity.class);
		intent.putExtra(FileManagerFragment.ROOT_PATH_KEY, startPath);
		intent.putExtra(FileManagerFragment.START_PATH_KEY, startPath);
		intent.putExtra(FileManagerFragment.FILENAME_REGEX_KEY, regex);
		intent.putExtra(FileManagerFragment.ICONE_ID_KEY, iconId);
		intent.putExtra(FileManagerFragment.INIT_FILENAME_KEY, mContext.getString(R.string.init_file_name));
		int actionFlags = FileManagerFragment.SAVE_ACTION_FLAG;
		intent.putExtra(FileManagerFragment.ACTION_FLAGS_KEY, actionFlags);
		int menuFlags = FileManagerFragment.DETAILS_MENU_FLAG | FileManagerFragment.NEW_FOLDER_MENU_FLAG | FileManagerFragment.RENAME_MENU_FLAG;
		intent.putExtra(FileManagerFragment.MENU_FLAGS_KEY, menuFlags);
		startActivityForResult(intent, SAVE_FILE_REQUEST_CODE);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == OPEN_FILE_REQUEST_CODE) {
			if ((data == null) || resultCode != Activity.RESULT_OK) {
				return;
			}
			String directory = data.getStringExtra(FileManagerFragment.DIRECTORY_KEY);
			String filename = data.getStringExtra(FileManagerFragment.FILENAME_KEY);
			String message = mContext.getString(R.string.open_file_selected_format, filename, directory);

			MessageDialogFragment messageDialog = MessageDialogFragment.newInstance(null, message, true);
			messageDialog.show(getFragmentManager(), MESSAGE_DIALOG_TAG);
		}
		
		if (requestCode == SAVE_FILE_REQUEST_CODE) {
			if ((data == null) || resultCode != Activity.RESULT_OK) {
				return;
			}
			String directory = data.getStringExtra(FileManagerFragment.DIRECTORY_KEY);
			String filename = data.getStringExtra(FileManagerFragment.FILENAME_KEY);
			String message = mContext.getString(R.string.save_file_selected_format, filename, directory);
			
			MessageDialogFragment messageDialog = MessageDialogFragment.newInstance(null, message, true);
			messageDialog.show(getFragmentManager(), MESSAGE_DIALOG_TAG);
		}
	}

}
