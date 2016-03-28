package com.http;

import android.content.Context;
import android.widget.ImageView;
import com.activity.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;


public class ImgLoader {
	       
    private ImageLoader imageLoader;  
 
    private DisplayImageOptions options;  
    
    public ImgLoader(Context context) {  
        // TODO Auto-generated constructor stub  

        imageLoader = ImageLoader.getInstance();  
        
        options = new DisplayImageOptions.Builder()  
        .showImageOnLoading(R.drawable.showme) // 设置图片下载期间显示的图片  
        .showImageForEmptyUri(R.drawable.showme) // 设置图片Uri为空或是错误的时候显示的图片  
        .showImageOnFail(R.drawable.showme) // 设置图片加载或解码过程中发生错误显示的图片  
        .cacheInMemory(true) // 设置下载的图片是否缓存在内存中  
        .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中  
    //    .displayer(new RoundedBitmapDisplayer(20)) // 设置成圆角图片  
        .build(); // 构建完成  
    }  
  
    public void display(ImageView container,String url){//外部接口函数  
    	
    	try{
    		imageLoader.displayImage(url, container, options);  
    	}catch(Exception e){
    		url="http://79763.vhost82.cloudvhost.net/images/1440847152798a1745.jpg";
    		imageLoader.displayImage(url, container, options);  
    	}
    }  

} 

