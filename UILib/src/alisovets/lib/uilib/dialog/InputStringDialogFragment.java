package alisovets.lib.uilib.dialog;

import alisovets.lib.uilib.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.InputType;
import android.widget.EditText;

/**
 * Creates a dialog to insert string
 * @author Alexander Lisovets
 *
 */
public class InputStringDialogFragment extends DialogFragment implements OnClickListener {
		public final static String TITLE_KEY = "title";  
		public final static String MESSAGE_KEY = "message";
		public final static String HINT_KEY = "hint";
		public final static String INIT_TEXT_KEY = "init_text";

	    private EditText mFileNameEditText;
	    
	    /**
	     * Creates new dialog 
	     * @param titleResId - resource id of a title string 
	     * @param messageResId - resource id of a message string
 	     * @param hintResId - resource id of a hint 
	     * @param initText - initial text 
	     * @return the created dialog fragment 
	     */
	    public static InputStringDialogFragment newInstance(int titleResId, int messageResId, int hintResId, String initText) { 
	    	InputStringDialogFragment frag = new InputStringDialogFragment();
	        Bundle args = new Bundle();
	        args.putInt(TITLE_KEY, titleResId);
	        args.putInt(MESSAGE_KEY, messageResId);
	        args.putInt(HINT_KEY, hintResId);
	        args.putString(INIT_TEXT_KEY, initText);
	        frag.setArguments(args);
	        return frag;
	    }

	    

	    @Override
	    public Dialog onCreateDialog(Bundle savedInstanceState) {
	    	Bundle args = getArguments();
	    	int titleResId = args.getInt(TITLE_KEY);
	    	int messageResId = args.getInt(MESSAGE_KEY);
	    	int hintResId = args.getInt(HINT_KEY);
	    	String filename = args.getString(INIT_TEXT_KEY);
	    	
	        mFileNameEditText = new EditText(getActivity());
	        mFileNameEditText.setInputType(InputType.TYPE_CLASS_TEXT);
	        mFileNameEditText.setHint(hintResId);
	        mFileNameEditText.setText(filename);
	        

	        return new AlertDialog.Builder(getActivity()).setTitle(titleResId).setMessage(messageResId)
	                .setPositiveButton(R.string.ok_btn_title, this).setNegativeButton(R.string.cancel_btn_title, null).setView(mFileNameEditText).create();

	    }

	    @Override
	    public void onClick(DialogInterface dialog, int position) {
	        TextReceivable nameReceivable = (TextReceivable)getActivity();
	        nameReceivable.receiveName(mFileNameEditText.getText().toString());
	        dialog.dismiss();
	    }	
	    
	   
}
