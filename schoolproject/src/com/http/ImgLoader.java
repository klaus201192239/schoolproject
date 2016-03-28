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
        .showImageOnLoading(R.drawable.showme) // ����ͼƬ�����ڼ���ʾ��ͼƬ  
        .showImageForEmptyUri(R.drawable.showme) // ����ͼƬUriΪ�ջ��Ǵ����ʱ����ʾ��ͼƬ  
        .showImageOnFail(R.drawable.showme) // ����ͼƬ���ػ��������з���������ʾ��ͼƬ  
        .cacheInMemory(true) // �������ص�ͼƬ�Ƿ񻺴����ڴ���  
        .cacheOnDisk(true) // �������ص�ͼƬ�Ƿ񻺴���SD����  
    //    .displayer(new RoundedBitmapDisplayer(20)) // ���ó�Բ��ͼƬ  
        .build(); // �������  
    }  
  
    public void display(ImageView container,String url){//�ⲿ�ӿں���  
    	
    	try{
    		imageLoader.displayImage(url, container, options);  
    	}catch(Exception e){
    		url="http://79763.vhost82.cloudvhost.net/images/1440847152798a1745.jpg";
    		imageLoader.displayImage(url, container, options);  
    	}
    }  

} 

