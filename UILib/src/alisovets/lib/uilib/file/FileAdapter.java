package alisovets.lib.uilib.file;


import alisovets.lib.uilib.R;
import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Extends the BaseAdapter class to work with files in ListView  
 * 
 * @author Alexander Lisovets, 2014
 *
 */
public class FileAdapter extends BaseAdapter{
	
	Context context;
	int layoutResourceId;
	FileItem[] data = null;
	
	public FileAdapter(Context context, int layoutResourceId, FileItem[] data) {
		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.data = data;
	}

	@Override
	public int getCount() {
		return data.length;
	}

	@Override
	public Object getItem(int position) {
		return data[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		FileHolder holder = null;

		if (row == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);

			holder = new FileHolder();
			holder.imgIcon = (ImageView) row.findViewById(R.id.imgIcon);
			holder.txtTitle = (TextView) row.findViewById(R.id.txtTitle);
			row.setTag(holder);
		} else {
			holder = (FileHolder) row.getTag();
		}

		FileItem item = data[position];
		holder.txtTitle.setText(item.getName());
		holder.imgIcon.setImageDrawable(item.getIcon());
		if(item.getType() == FileItem.FILE){
			holder.txtTitle.setTypeface(Typeface.DEFAULT);
		}
		else{
			holder.txtTitle.setTypeface(Typeface.DEFAULT_BOLD);
		}
		
		return row;
	}
	
	/**
	 * sets a data array
	 * @param data the array of the fileItems
	 */
	public void setData(FileItem[] data){
		this.data = data;
	}
	
	/*
	 * to hold a item view element
	 */
	private static class FileHolder {
		ImageView imgIcon;
		TextView txtTitle;

	}

}
