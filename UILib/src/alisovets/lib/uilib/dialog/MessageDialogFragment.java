package alisovets.lib.uilib.dialog;

import alisovets.lib.uilib.R;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * Creates message dialog 
 * 
 * @author Alexander Lisovets
 *
 */
public class MessageDialogFragment extends DialogFragment {
	public final static String TITLE_ID_KEY = "title_id";
	public final static String TITLE_KEY = "title";
	public final static String MESSAGE_KEY = "message";
	public final static String OK_BUTTON_KEY = "ok_button";

	/**
	 * Creates and return new message dialog
	 * @param titleResId - a resource Id of a title string 
	 * @param messageResId - a resourse id of a message string
	 * @param isOkButton - if true, shows ok button  
	 * @return created a dialog fragment
	 */
	public static MessageDialogFragment newInstance(int titleResId, int messageResId, boolean isOkButton) {
		MessageDialogFragment frag = new MessageDialogFragment();
		Bundle args = new Bundle();
		args.putInt(TITLE_ID_KEY, titleResId);
		args.putInt(MESSAGE_KEY, messageResId);
		args.putBoolean(OK_BUTTON_KEY, isOkButton);
		frag.setArguments(args);
		return frag;
	}

	/**
	 * Creates and return new message dialog
	 * @param title - a title string
	 * @param message - a message title
	 * @param isOkButton - if true, shows ok button  
	 * @return created a dialog fragment
	 */
	public static MessageDialogFragment newInstance(String title, String message, boolean isOkButton) {
		MessageDialogFragment frag = new MessageDialogFragment();
		Bundle args = new Bundle();
		args.putString(TITLE_KEY, title);
		args.putString(MESSAGE_KEY, message);
		args.putBoolean(OK_BUTTON_KEY, isOkButton);
		frag.setArguments(args);
		return frag;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Bundle args = getArguments();

		Builder builder;
		int titleResId = args.getInt(TITLE_ID_KEY);
		if (titleResId > 0) {
			int messageResId = args.getInt(MESSAGE_KEY);
			builder = new AlertDialog.Builder(getActivity()).setTitle(titleResId).setMessage(messageResId);
		} else {
			String title = args.getString(TITLE_KEY);
			String message = args.getString(MESSAGE_KEY);
			builder = new AlertDialog.Builder(getActivity()).setTitle(title).setMessage(message);

		}
		if (args.getBoolean(OK_BUTTON_KEY, false)) {
			builder.setPositiveButton(R.string.ok_btn_title, null);
		}
		return builder.create();
	}

}
