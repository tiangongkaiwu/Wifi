package cn.hudun.wifi.pwd.ui.fragment;


import cn.hudun.wifi.pwd.R;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class FourFragment extends Fragment {
	
	private CheckBox isdown;// 是否安装好搜
	private CheckStateListener listenr;
	private ImageView iv;

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		listenr = (CheckStateListener) getActivity();
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.four, null);
		// 是否安装360好搜
		iv=(ImageView) view.findViewById(R.id.iv);
		isdown = (CheckBox) view.findViewById(R.id.isdown);
		
		isdown.setChecked(true);
		isdown.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				listenr.checkState(arg1);
			}
		});

		return view;
	}
	
	

	public interface CheckStateListener {
		void checkState(boolean b);
	}
	

	
	

}
