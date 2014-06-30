package alisovets.lib.uilib.file;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import alisovets.lib.uilib.R;
import alisovets.lib.uilib.dialog.AboutDialog;
import alisovets.lib.uilib.dialog.ConfirmDialogFragment;
import alisovets.lib.uilib.dialog.FailOpenFileDialog;
import alisovets.lib.uilib.dialog.InputStringDialogFragment;
import alisovets.lib.uilib.dialog.MessageDialogFragment;
import alisovets.lib.uilib.dialog.NoApplicationDialog;
import alisovets.lib.uilib.util.FileUtil;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 
 * A Fragment to manage the filesystem
 * 
 * @author Alexander Lisovets
 * 
 */
public class FileManagerFragment extends Fragment implements OnItemClickListener {

	private static final String TAG = "OpenFileFragment log";

	public static final String ROOT_PATH_KEY = "rootPath";
	public static final String START_PATH_KEY = "startPath";
	public static final String FILENAME_REGEX_KEY = "filenameRegex";
	public static final String ICONE_ID_KEY = "fileIcon";
	public static final String ACTION_FLAGS_KEY = "actionFlags";
	public static final String MENU_FLAGS_KEY = "menuFlags";
	public static final String INIT_FILENAME_KEY = "initFilename";
	public static final String DIRECTORY_KEY = "directory";
	public static final String FILENAME_KEY = "filename";
	public static final String FILE_PREFIX = "file://";

	public static final int ACTION_FLAGS = 7;

	public static final int DEFAULT_ACTION_FLAG = 0;

	/** if it is set then the choice is for save, the save button is present. */
	public static final int SAVE_ACTION_FLAG = 1;

	/** if it is set then the choice is for open */
	public static final int OPEN_ACTION_FLAG = 2;

	/** if it is set then a directory can be selected */
	public static final int SELECT_DIR_ACTION_FLAG = 4;

	/** if it is set hidden files are shown */
	public static final int SHOW_HIDDEN_ACTION_FLAG = 8;

	/** if it is set then all menu items will be shown. */
	public static final int ALL_MENU_FLAG = 0xff;

	/** if it is set then the Cut-Copy-Paste menu items will be shown. */
	public static final int COPY_PASTE_MENU_FLAG = 1;

	/** if it is set then the Rename menu item will be shown. */
	public static final int RENAME_MENU_FLAG = 2;

	/** if it is set then the New Folder menu item will be shown. */
	public static final int NEW_FOLDER_MENU_FLAG = 4;

	/** if it is set then the Delete menu item will be shown. */
	public static final int DELETE_MENU_FLAG = 8;

	/** if it is set then the Details menu item will be shown. */
	public static final int DETAILS_MENU_FLAG = 16;

	/** if it is set then the 'Show hidden' menu item will be shown. */
	public static final int SHOW_HIDDEN_MENU_FLAG = 32;

	private static final String DEFAULT_START_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();

	private static final String DEFAULT_FILENAME_REGEX = ".*";
	private static final int DEFAULT_ICONE_ID = R.drawable.ic_unknown_doc;

	public static final String NO_APP_DLG_TAG = "no_apps_to_open_dlg";
	public static final String FAIL_OPEN_FILE_DLG_TAG = "fail_open_file_dlg";
	public static final String RENAME_FILE_DLG_TAG = "rename_file_dlg";
	public static final String FAIL_RENAME_FILE_DLG_TAG = "fail_rename_file_dlg";
	public static final String FAIL_DELETE_FILE_DLG_TAG = "fail_delete_file_dlg";
	public static final String FAIL_COPY_FILE_DLG_TAG = "fail_copy_file_dlg";
	public static final String NEW_FOLDER_DLG_TAG = "new_folder_dlg";
	public static final String FAIL_CREATE_FOLDER_DLG_TAG = "fail_folder_creating_dlg";
	public static final String DELETE_FILE_DLG_TAG = "delete_file";
	public static final String COPY_FILE_DLG_TAG = "copy_file";
	public static final String ABOUT_DLG_TAG = "aboud_dlg";

	private ListView mListView;
	private FileAdapter mAdapter;
	private EditText mFileNameEdit;
	private TextView mPathTextView;

	private File mCurrentFile;
	private File mRootPath;
	private File mHomePath;

	private String filenameRegex;
	private String mFilename;
	private int mTargetIconId;
	private int mActionFlags;
	private int mMenuItemFlags;
	private boolean mShowHiddenFlag;

	private MenuItem mMenuItemUp;
	private MenuItem mMenuItemHome;
	private MenuItem mMenuItemDone;
	private MenuItem mMenuItemShowHidden;

	private MenuItem mCopyMenuItem;
	private MenuItem mCutMenuItem;
	private MenuItem mPasteMenuItem;
	private MenuItem mRenameMenuItem;
	private MenuItem mNewFolderMenuItem;
	private MenuItem mDeleteMenuItem;
	private MenuItem mDetailsMenuItem;
	private MenuItem mLastMenuItem;
	private TextView mEmptyDirTextView;

	private FileItem mCurrentFileItem;
	private boolean mIsCut;
	private ProgressDialog mProgressDialog;
	private File mFileToCopy;

	FileItem data[];

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.uilib_file_selector_layout, container, false);

		getStringExtraParameters();

		data = readDirectory(mCurrentFile);
		mAdapter = new FileAdapter(getActivity(), R.layout.uilib_file_selector_list_item_layout, data);
		mListView = (ListView) rootView.findViewById(R.id.listView);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);
		registerForContextMenu(mListView);
		mEmptyDirTextView = (TextView) rootView.findViewById(R.id.emptyDirMessage);
		FrameLayout screenLayout = (FrameLayout) rootView.findViewById(R.id.screenLayout);
		registerForContextMenu(screenLayout);
		if (data.length == 0) {
			mEmptyDirTextView.setVisibility(View.VISIBLE);
		}

		mPathTextView = (TextView) rootView.findViewById(R.id.pathTextView);
		setCustomView();
		setHasOptionsMenu(true);
		return rootView;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

		FileItem item = data[position];
		if (item.getType() == FileItem.FILE) {
			clickFileProsessing(item);
			return;
		}

		mCurrentFile = new File(mCurrentFile, item.getName());
		showCurrentDirectory();

	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

		inflater.inflate(R.menu.uilib_file_menu, menu);
		super.onCreateOptionsMenu(menu, inflater);

		mMenuItemDone = menu.findItem(R.id.action_done);
		mMenuItemUp = menu.findItem(R.id.action_folder_up);
		mMenuItemShowHidden = menu.findItem(R.id.action_hidden);
		mMenuItemHome = menu.findItem(R.id.action_home);

		mShowHiddenFlag = (mActionFlags & SHOW_HIDDEN_ACTION_FLAG) != 0;
		if (((mMenuItemFlags & SHOW_HIDDEN_MENU_FLAG) != SHOW_HIDDEN_MENU_FLAG)) {
			mMenuItemShowHidden.setVisible(false);
		} else {
			mMenuItemShowHidden.setVisible(true);
			if (mShowHiddenFlag) {
				mMenuItemShowHidden.setTitle(R.string.uilib_hide_hidden_menu_item);
			} else {
				mMenuItemShowHidden.setTitle(R.string.uilib_show_hidden_menu_item);
			}
		}
		setActionButtonVisible();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.getItemId() == mMenuItemUp.getItemId()) {
			clickUp();
			return true;
		}

		if (item.getItemId() == mMenuItemHome.getItemId()) {
			clickHome();
			return true;
		}

		if (item.getItemId() == mMenuItemDone.getItemId()) {
			clickDone();
			return true;
		}

		if (item.getItemId() == mMenuItemShowHidden.getItemId()) {
			clickShowHidden();
			return true;
		}

		if (item.getItemId() == R.id.action_about_app) {
			new AboutDialog().show(getFragmentManager(), ABOUT_DLG_TAG);
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {

		if (menu.size() > 0) {
			return;
		}

		boolean itemSelected = false;
		boolean canWrite = false;
		boolean canRead = false;
		if (v == mListView) {

			int position = ((AdapterContextMenuInfo) menuInfo).position;
			mCurrentFileItem = (FileItem) mAdapter.getItem(position);
			itemSelected = true;
			File file = new File(mCurrentFile, mCurrentFileItem.getName());
			canWrite = file.canWrite();
			canRead = file.canRead();

		} else {
			mCurrentFileItem = null;
		}

		if (canRead && (mMenuItemFlags & COPY_PASTE_MENU_FLAG) != 0) {

			mCopyMenuItem = menu.add(R.string.uilib_copy_menu_tile);
		}

		if (canWrite && (mMenuItemFlags & COPY_PASTE_MENU_FLAG) != 0) {
			mCutMenuItem = menu.add(R.string.uilib_cut_menu_tile);
		}

		if ((mMenuItemFlags & COPY_PASTE_MENU_FLAG) != 0) {
			if (FileUtil.getFileFromClipboard(getActivity(), false) != null) {
				mPasteMenuItem = menu.add(R.string.uilib_paste_menu_tile);
			}
		}

		if (canWrite && (mMenuItemFlags & RENAME_MENU_FLAG) != 0) {
			mRenameMenuItem = menu.add(R.string.uilib_rename_menu_tile);
		}

		if ((mMenuItemFlags & NEW_FOLDER_MENU_FLAG) != 0) {
			mNewFolderMenuItem = menu.add(R.string.uilib_new_folder_menu_tile);
		}
		if (canWrite && (mMenuItemFlags & DELETE_MENU_FLAG) != 0) {
			mDeleteMenuItem = menu.add(R.string.uilib_delete_menu_tile);
		}
		if (itemSelected && (mMenuItemFlags & DETAILS_MENU_FLAG) != 0) {
			mDetailsMenuItem = menu.add(R.string.uilib_details_menu_tile);
		}

		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {

		mLastMenuItem = item;
		if (item == mRenameMenuItem) {
			Log.d(TAG, "Rename");
			InputStringDialogFragment filenameDialog = InputStringDialogFragment.newInstance(R.string.uilib_rename_dlg_title,
					R.string.uilib_rename_file_dlg_message, R.string.uilib_rename_dlg_hint, mCurrentFileItem.getName());
			filenameDialog.show(getFragmentManager(), RENAME_FILE_DLG_TAG);
			return true;
		}

		if (item == mDetailsMenuItem) {
			obtainFileDetails();
			return true;
		}

		if (item == mNewFolderMenuItem) {
			Log.d(TAG, "New folder");
			InputStringDialogFragment filenameDialog = InputStringDialogFragment.newInstance(R.string.uilib_new_folder_title,
					R.string.uilib_new_folder_message, R.string.uilib_rename_dlg_hint, getString(R.string.uilib_new_folder_name));
			filenameDialog.show(getFragmentManager(), NEW_FOLDER_DLG_TAG);

			return true;
		}

		if (item == mDeleteMenuItem) {
			String title;
			String question;
			if (new File(mCurrentFile, mCurrentFileItem.getName()).isDirectory()) {
				title = getString(R.string.uilib_delete_dir_dlg_title);
				question = getString(R.string.uilib_delete_dir_dlg_quest) + " " + mCurrentFileItem.getName() + "?";
			} else {
				title = getString(R.string.uilib_delete_file_dlg_title);
				question = getString(R.string.uilib_delete_file_dlg_quest) + " " + mCurrentFileItem.getName() + "?";
			}

			ConfirmDialogFragment confirmDialog = ConfirmDialogFragment.newInstance(title, question);
			confirmDialog.show(getFragmentManager(), DELETE_FILE_DLG_TAG);
			return true;
		}

		if (item == mCopyMenuItem) {
			Log.d(TAG, "menu copy");

			mIsCut = false;
			FileUtil.copyToClipboard(getActivity(), new File(mCurrentFile, mCurrentFileItem.getName()));
			return true;
		}

		if (item == mCutMenuItem) {
			mIsCut = true;
			FileUtil.copyToClipboard(getActivity(), new File(mCurrentFile, mCurrentFileItem.getName()));
			return true;
		}

		if (item == mPasteMenuItem) {
			copyPaste();
			return true;
		}

		return super.onContextItemSelected(item);
	}

	/*
	 * gets parameters from the Intent object
	 */
	private void getStringExtraParameters() {
		Intent intent = getActivity().getIntent();

		String currentPath = intent.getStringExtra(START_PATH_KEY);
		if (currentPath == null) {
			currentPath = DEFAULT_START_PATH;
		}

		mCurrentFile = new File(currentPath);
		mHomePath = new File(currentPath);

		String rootPath = intent.getStringExtra(ROOT_PATH_KEY);
		if (rootPath == null) {
			if (currentPath.equals(DEFAULT_START_PATH)) {
				rootPath = mCurrentFile.getParent();
			} else {
				rootPath = currentPath;
			}
		}
		mRootPath = new File(rootPath);

		filenameRegex = intent.getStringExtra(FILENAME_REGEX_KEY);
		if (filenameRegex == null) {
			filenameRegex = DEFAULT_FILENAME_REGEX;
		}

		mFilename = intent.getStringExtra(INIT_FILENAME_KEY);
		if (mFilename == null) {
			mFilename = "";
		}

		mTargetIconId = intent.getIntExtra(ICONE_ID_KEY, DEFAULT_ICONE_ID);
		mActionFlags = intent.getIntExtra(ACTION_FLAGS_KEY, DEFAULT_ACTION_FLAG);
		mMenuItemFlags = intent.getIntExtra(MENU_FLAGS_KEY, ALL_MENU_FLAG);

	}

	/*
	 * set initial parameters of the action bar
	 */
	private void setCustomView() {

		if ((mActionFlags & SAVE_ACTION_FLAG) == SAVE_ACTION_FLAG) {
			ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
			actionBar.setCustomView(R.layout.uilib_edit_file_name_layout);
			actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME);
			mFileNameEdit = (EditText) actionBar.getCustomView();
			mFileNameEdit.setText(mFilename);
		}
	}

	/**
	 * changes showing of hidden files
	 */
	private void clickShowHidden() {

		mShowHiddenFlag = !mShowHiddenFlag;

		if (mShowHiddenFlag) {
			mMenuItemShowHidden.setTitle(R.string.uilib_hide_hidden_menu_item);
		} else {
			mMenuItemShowHidden.setTitle(R.string.uilib_show_hidden_menu_item);
		}

		showCurrentDirectory();
	}

	/*
	 * process click on the Done button set selected filename and directory in
	 * the Intent as an extra parameter end closes current activity
	 */
	private void clickDone() {

		if ((mActionFlags & ACTION_FLAGS) == 0) {
			return;
		}

		Intent returnIntent = new Intent();
		returnIntent.putExtra(DIRECTORY_KEY, mCurrentFile.getAbsolutePath());

		if ((mActionFlags & SAVE_ACTION_FLAG) == SAVE_ACTION_FLAG) {
			String fileName = mFileNameEdit.getText().toString().trim();
			returnIntent.putExtra(FILENAME_KEY, fileName);
		}

		getActivity().setResult(Activity.RESULT_OK, returnIntent);
		getActivity().finish();
	}

	/*
	 * process click an item in the file
	 */
	private void clickFileProsessing(FileItem item) {

		if ((mActionFlags & ACTION_FLAGS) == DEFAULT_ACTION_FLAG) {
			Log.d(TAG, "try open file");
			openFile(new File(mCurrentFile, item.getName()));
			return;
		}

		if ((mActionFlags & OPEN_ACTION_FLAG) == OPEN_ACTION_FLAG) {

			Intent returnIntent = new Intent();
			returnIntent.putExtra("directory", mCurrentFile.getAbsolutePath());
			returnIntent.putExtra("filename", item.getName());
			getActivity().setResult(Activity.RESULT_OK, returnIntent);
			getActivity().finish();
			return;
		}

		if ((mActionFlags & SAVE_ACTION_FLAG) == SAVE_ACTION_FLAG) {
			mFileNameEdit.setText(item.getName());
			return;
		}

	}

	/*
	 * process click on the Up button
	 */
	private void clickUp() {

		if (mCurrentFile.equals(mRootPath)) {
			return;
		}

		File newDirectory = mCurrentFile.getParentFile();
		if (newDirectory == null) {
			return;
		}

		mCurrentFile = newDirectory;
		showCurrentDirectory();
	}

	/*
	 * process click on the Home button
	 */
	private void clickHome() {
		if (mCurrentFile.equals(mHomePath)) {
			return;
		}
		mCurrentFile = mHomePath;
		showCurrentDirectory();

	}

	/*
	 * reinits file list
	 */
	private void showCurrentDirectory() {

		FileItem[] fileItems = readDirectory(mCurrentFile);
		if (fileItems == null) {
			fileItems = new FileItem[0];
		}

		if (fileItems.length == 0) {
			mEmptyDirTextView.setVisibility(View.VISIBLE);
		} else {
			mEmptyDirTextView.setVisibility(View.GONE);
		}

		data = fileItems;
		mAdapter.setData(data);
		mAdapter.notifyDataSetChanged();
		setActionButtonVisible();

	}

	/*
	 * set button visibilities depend on extra parameters that was passed in the
	 * Intent
	 */
	private void setActionButtonVisible() {

		mPathTextView.setText(mCurrentFile.getAbsolutePath());

		if ((mActionFlags & SELECT_DIR_ACTION_FLAG) == SELECT_DIR_ACTION_FLAG) {
			mMenuItemDone.setVisible(true);
		} else if ((mActionFlags & SAVE_ACTION_FLAG) == SAVE_ACTION_FLAG) {
			if (TextUtils.isEmpty(mFileNameEdit.getText().toString())) {
				mMenuItemDone.setVisible(false);
			} else {
				mMenuItemDone.setVisible(true);
			}
		} else {
			mMenuItemDone.setVisible(false);
		}

		mMenuItemUp.setVisible(true);
		mMenuItemHome.setVisible(true);

	}

	/**
	 * creates and return a list of the specified directory content
	 * 
	 * @param path
	 *            - the path to directory to get content list
	 * @return list of the FileItem objects
	 */
	protected FileItem[] readDirectory(File path) {

		File[] files = path.listFiles();
		if (files == null) {
			return new FileItem[0];
		}

		FileItem[] items = new FileItem[files.length];
		int count = 0;
		for (int i = 0; i < files.length; i++) {
			if ((files[i].isHidden()) && !mShowHiddenFlag) {
				continue;
			}

			if (files[i].isDirectory()) {
				items[count++] = new FileItem(getResources().getDrawable(R.drawable.ic_folder_grey), files[i].getName(), FileItem.DIRECTORY);
				continue;
			}

			if (((mActionFlags & ACTION_FLAGS) != DEFAULT_ACTION_FLAG) && files[i].getName().matches(filenameRegex)) {
				Drawable icon = getIconForFile(files[i]);
				if (icon != null) {
					items[count++] = new FileItem(icon, files[i].getName(), FileItem.FILE);
				} else {
					items[count++] = new FileItem(getResources().getDrawable(mTargetIconId), files[i].getName(), FileItem.FILE);
				}
				continue;
			}

			if ((mActionFlags & ACTION_FLAGS) == DEFAULT_ACTION_FLAG) {
				Drawable icon = getIconForFile(files[i]);
				if (icon != null) {
					items[count++] = new FileItem(icon, files[i].getName(), FileItem.FILE);
				} else {
					items[count++] = new FileItem(getResources().getDrawable(DEFAULT_ICONE_ID), files[i].getName(), FileItem.FILE);
				}
			}
		}

		if (count < items.length) {
			FileItem[] tmpItem = new FileItem[count];
			for (int i = 0; i < count; i++) {
				tmpItem[i] = items[i];
			}
			items = tmpItem;
		}
		Arrays.sort(items);
		return items;
	}

	/**
	 * Gets and returns system icon for the specified in the parameter file
	 * depending on extension
	 * 
	 * @param file
	 *            - the file for which to get the icon
	 * @return Drawable object of the icon
	 */
	public Drawable getIconForFile(File file) {

		MimeTypeMap mime = MimeTypeMap.getSingleton();
		String mimeType = mime.getMimeTypeFromExtension(FileUtil.getFileExtension(file));
		if (mimeType == null) {
			return null;
		}

		Drawable icon = null;

		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file), mimeType);

		PackageManager packageManager = getActivity().getPackageManager();
		List<ResolveInfo> matches = packageManager.queryIntentActivities(intent, 0);

		for (ResolveInfo match : matches) {
			icon = match.loadIcon(packageManager);
			if (icon != null) {
				return icon;
			}
		}

		return null;
	}

	/**
	 * open application to show or process specified file
	 * 
	 * @param file
	 *            file to open
	 */
	private void openFile(File file) {

		MimeTypeMap mime = MimeTypeMap.getSingleton();
		String mimeType = mime.getMimeTypeFromExtension(FileUtil.getFileExtension(file));

		if (mimeType == null) {
			NoApplicationDialog messageDialog = new NoApplicationDialog();
			messageDialog.show(getFragmentManager(), NO_APP_DLG_TAG);
			return;
		}
		try {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setDataAndType(Uri.fromFile(file), mimeType);
			startActivityForResult(intent, 0);
		} catch (Exception e) {
			FailOpenFileDialog messageDialog = new FailOpenFileDialog();
			messageDialog.show(getFragmentManager(), FAIL_OPEN_FILE_DLG_TAG);
		}
	}

	/*
	 * Copies the specified by the parameter file in the current directory.
	 * Opens message dialog if fail
	 */
	private void copyFileInCurrentDirectory(File file) {
		String fileName = file.getName();
		File copyFile = new File(mCurrentFile, fileName);
		try {
			FileUtil.copyFile(file, copyFile, false);
			showCurrentDirectory();
		} catch (IOException e) {
			int messageId;
			int titleId = R.string.uilib_fail_copy_dlg_title;
			if (file.isDirectory()) {
				messageId = R.string.uilib_fail_copy_dir_dlg_message;
			} else {
				messageId = R.string.uilib_fail_copy_file_dlg_message;
			}
			MessageDialogFragment messageDialog = MessageDialogFragment.newInstance(titleId, messageId, true);
			messageDialog.show(getFragmentManager(), FAIL_COPY_FILE_DLG_TAG);
		}
	}

	/*
	 * copies the file the path to which is specified in a clipboard
	 */
	private void copyPaste() {
		File file = FileUtil.getFileFromClipboard(getActivity(), mIsCut);

		if (file == null) {
			return;
		}

		String fileName = file.getName();
		File copyFile = new File(mCurrentFile, fileName);
		if (mIsCut) {
			// replace
			if ((copyFile.getAbsolutePath() + "/").startsWith(file.getAbsolutePath() + "/")) {
				// try to insert in itself or a subdirectory
				Log.d(TAG, "copyFile: " + copyFile.getAbsolutePath() + "  file: " + file.getAbsolutePath());
				MessageDialogFragment messageDialog = MessageDialogFragment.newInstance(R.string.uilib_fail_replace_dlg_title,
						R.string.uilib_fail_replace_dir_in_itself_dlg_message, true);
				messageDialog.show(getFragmentManager(), FAIL_COPY_FILE_DLG_TAG);
				return;

			}

			if (copyFile.exists()) {
				// copy and delete the source file;
				ConfirmDialogFragment confirmDialog;
				if (file.isDirectory()) {
					confirmDialog = ConfirmDialogFragment.newInstance(R.string.uilib_replace_dlg_title, R.string.uilib_copy_exist_dir_dlg_quest);
				} else {
					confirmDialog = ConfirmDialogFragment.newInstance(R.string.uilib_replace_dlg_title, R.string.uilib_copy_exist_file_dlg_quest);
				}
				confirmDialog.show(getFragmentManager(), COPY_FILE_DLG_TAG);
				mFileToCopy = file;
				return;
			}

			if (file.renameTo(copyFile)) {
				showCurrentDirectory();
				return;
			}

			MessageDialogFragment messageDialog = MessageDialogFragment.newInstance(R.string.uilib_fail_replace_dlg_title,
					R.string.uilib_fail_replace_file_dlg_message, true);
			messageDialog.show(getFragmentManager(), FAIL_COPY_FILE_DLG_TAG);
			return;
		}

		// copy
		if (copyFile.exists()) {
			int titleId;
			int questionId;
			if (copyFile.isDirectory()) {
				titleId = R.string.uilib_copy_dir_dlg_title;
				questionId = R.string.uilib_copy_exist_dir_dlg_quest;
			} else {
				titleId = R.string.uilib_copy_file_dlg_title;
				questionId = R.string.uilib_copy_exist_file_dlg_quest;
			}
			ConfirmDialogFragment confirmDialog = ConfirmDialogFragment.newInstance(titleId, questionId);
			confirmDialog.show(getFragmentManager(), COPY_FILE_DLG_TAG);
			mFileToCopy = file;
			return;
		}

		copyFileInCurrentDirectory(file);
		return;

	}

	/*
	 * Starts asynchronous recursive copying the directory
	 */
	private void recursiveReplace() {

		mProgressDialog = new ProgressDialog(getActivity(), ProgressDialog.STYLE_HORIZONTAL);
		RecurciveRepalceTask task = new RecurciveRepalceTask();
		mProgressDialog.setOnDismissListener(task);
		mProgressDialog.setMessage(getString(R.string.uilib_processing_dlg_msg));
		mProgressDialog.show();
		task.execute();

	}

	/*
	 * Starts asynchronous recursive getting file details
	 */
	private void obtainFileDetails() {

		mProgressDialog = new ProgressDialog(getActivity(), ProgressDialog.STYLE_HORIZONTAL);
		FileDetailsGetterTask task = new FileDetailsGetterTask();
		mProgressDialog.setOnDismissListener(task);
		mProgressDialog.setMessage(getString(R.string.uilib_processing_dlg_msg));
		mProgressDialog.show();
		task.execute();

	}

	public void confirm() {
		if (mLastMenuItem.equals(mDeleteMenuItem)) {

			try {
				FileUtil.deleteRecursively(new File(mCurrentFile, mCurrentFileItem.getName()));
				showCurrentDirectory();
			} catch (IOException e) {
				MessageDialogFragment messageDialog = MessageDialogFragment.newInstance(R.string.uilib_fail_delete_dlg_title,
						R.string.uilib_fail_delete_file_dlg_message, true);
				messageDialog.show(getFragmentManager(), FAIL_DELETE_FILE_DLG_TAG);
			}
			return;
		}

		if (mLastMenuItem.equals(mPasteMenuItem)) {
			if (mIsCut) {
				recursiveReplace();
			}
		}

	}

	/*
	 * completes the last operation using the file name from passed parameter
	 */
	public void receiveFileName(String filename) {

		if(mLastMenuItem == null){
			return;
		}
		
		if (mLastMenuItem.equals(mRenameMenuItem)) {
			File file = new File(mCurrentFile, mCurrentFileItem.getName());
			File newFile = new File(mCurrentFile, filename);
			if (file.renameTo(newFile)) {
				showCurrentDirectory();
				return;
			}
			MessageDialogFragment.newInstance(R.string.uilib_fail_rename_dlg_title, R.string.uilib_fail_rename_file_dlg_message, true).show(
					getFragmentManager(), FAIL_RENAME_FILE_DLG_TAG);

			return;
		}

		if (mLastMenuItem.equals(mNewFolderMenuItem)) {
			File directory = new File(mCurrentFile, filename);
			if (directory.exists()) {
				MessageDialogFragment.newInstance(R.string.uilib_fail_create_dir_dlg_title, R.string.uilib_directory_exists_dlg_message, true).show(
						getFragmentManager(), FAIL_CREATE_FOLDER_DLG_TAG);
				return;
			}

			if (directory.mkdir()) {
				showCurrentDirectory();
				return;
			}
			MessageDialogFragment.newInstance(R.string.uilib_fail_create_dir_dlg_title, R.string.uilib_fail_create_dir_dlg_message, true).show(
					getFragmentManager(), FAIL_CREATE_FOLDER_DLG_TAG);
		}
	}

	/**
	 * processes hardware Back button pressing
	 * 
	 * @return true if not need standard processing
	 */
	public boolean backPressed() {
		if (mCurrentFile.equals(mRootPath)) {
			return true;
		}
		clickUp();
		return false;
	}

	/**
	 * 
	 * To asynchronously replace a directory
	 * 
	 */
	class RecurciveRepalceTask extends AsyncTask<Void, Void, Boolean> implements OnDismissListener {

		private Thread thread;
		private boolean isUserInterrupted;

		@Override
		protected Boolean doInBackground(Void... params) {
			thread = Thread.currentThread();
			try {
				FileUtil.replaceRecursively(mFileToCopy, new File(mCurrentFile, mFileToCopy.getName()), false);
				return true;
			} catch (IOException e) {
				return false;
			}
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if (isUserInterrupted) {
				return;
			}
			if (result) {
				Log.d(TAG, "Result OK!!!");
			} else {
				Log.d(TAG, "Replace from " + mFileToCopy.getAbsolutePath() + " to directory: " + mCurrentFile.getAbsolutePath() + " Result FAIL!!!");
				int messageId;
				int titleId = R.string.uilib_fail_replace_dlg_title;
				if (mFileToCopy.isDirectory()) {
					messageId = R.string.uilib_fail_replace_dir_dlg_message;
				} else {
					messageId = R.string.uilib_fail_replace_file_dlg_message;
				}
				MessageDialogFragment messageDialog = MessageDialogFragment.newInstance(titleId, messageId, true);
				messageDialog.show(getFragmentManager(), FAIL_COPY_FILE_DLG_TAG);

			}

			mProgressDialog.dismiss();
			mProgressDialog = null;
		}

		@Override
		public void onDismiss(DialogInterface dialog) {

			isUserInterrupted = true;
			thread.interrupt();
		}
	}

	/**
	 * To asynchronously get details of a file
	 */
	class FileDetailsGetterTask extends AsyncTask<Void, Void, FileUtil.FileDetails> implements OnDismissListener {
		private Thread thread;
		private boolean isUserInterrupted;

		@Override
		protected FileUtil.FileDetails doInBackground(Void... params) {

			thread = Thread.currentThread();
			FileUtil.FileDetails details = FileUtil.createDetails(new File(mCurrentFile, mCurrentFileItem.getName()));
			return details;
		}

		@Override
		protected void onPostExecute(FileUtil.FileDetails details) {
			super.onPostExecute(details);
			Log.d(TAG, "onPostExecute()" + details);
			if (isUserInterrupted) {
				return;
			}

			String detailString;
			String allowRead = details.canRead ? getString(R.string.yes) : getString(R.string.no);
			String allowWrite = details.canWrite ? getString(R.string.yes) : getString(R.string.no);
			if (details.isDirectory) {
				detailString = String.format(getString(R.string.uilib_dir_details), details.filename, details.parentFolder, details.size, details.folderCount,
						details.fileCount, details.lastModifiedDate, allowRead, allowWrite);
			} else {

				detailString = String.format(getString(R.string.uilib_file_details), details.filename, details.parentFolder, details.size,
						details.lastModifiedDate, allowRead, allowWrite);
			}
			detailString += String.format(getString(R.string.uilib_fs_details), details.freeSpace, details.totalSpace);

			MessageDialogFragment messageDialog = MessageDialogFragment.newInstance("Details", detailString, true);
			messageDialog.show(getFragmentManager(), FAIL_DELETE_FILE_DLG_TAG);

			mProgressDialog.dismiss();
			mProgressDialog = null;

		}

		@Override
		public void onDismiss(DialogInterface dialog) {
			Log.d(TAG, "onDismiss");
			isUserInterrupted = true;
			thread.interrupt();

		}

	}

}
