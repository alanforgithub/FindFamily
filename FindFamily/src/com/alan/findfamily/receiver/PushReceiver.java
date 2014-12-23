package com.alan.findfamily.receiver;

import com.jumei.findfamily.utils.ToastUtil;

import cn.bmob.push.PushConstants;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class PushReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		
		if(intent.getAction().equals(PushConstants.ACTION_MESSAGE)){
			String msg = intent.getStringExtra(PushConstants.EXTRA_PUSH_MESSAGE_STRING);
			ToastUtil.showToast(context, msg+".....");
		}

	}

}
