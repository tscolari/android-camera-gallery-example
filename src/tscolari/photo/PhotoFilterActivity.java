package tscolari.photo;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class PhotoFilterActivity extends Activity {
	
	private ImageView imageView;
	private Button buttonNewPic;
	private Button buttonImage;
	
	private Bitmap image;
	
	private static final int IMAGE_PICK 	= 1;
	private static final int IMAGE_CAPTURE 	= 2;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        this.imageView 		= (ImageView) this.findViewById(R.id.imageView);
        this.buttonNewPic 	= (Button) this.findViewById(R.id.button_camera);
        this.buttonImage 	= (Button) this.findViewById(R.id.button_from_phone);
        
        this.buttonImage.setOnClickListener(new ImagePickListener());
        this.buttonNewPic.setOnClickListener(new TakePictureListener());
    }
    
    /**
     * Receive the result from the startActivity
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	
    	if (resultCode == Activity.RESULT_OK) { 
	    	switch (requestCode) {
			case IMAGE_PICK:	
				this.imageFromGallery(resultCode, data);
				break;
			case IMAGE_CAPTURE:
				this.imageFromCamera(resultCode, data);
				break;
			default:
				break;
			}
    	}
    }
    
    /**
     * Update the imageView with new bitmap
     * @param newImage
     */
    private void updateImageView(Bitmap newImage) {
    	BitmapProcessor bitmapProcessor = new BitmapProcessor(newImage, 1000, 1000, 90);
    	
    	this.image = bitmapProcessor.getBitmap();
    	this.imageView.setImageBitmap(this.image);
    }
    
    /**
     * Image result from camera
     * @param resultCode
     * @param data
     */
    private void imageFromCamera(int resultCode, Intent data) {
    	this.updateImageView((Bitmap) data.getExtras().get("data"));
    }
    
    /**
     * Image result from gallery
     * @param resultCode
     * @param data
     */
    private void imageFromGallery(int resultCode, Intent data) {
    	Uri selectedImage = data.getData();
    	String [] filePathColumn = {MediaStore.Images.Media.DATA};
    	
    	Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
    	cursor.moveToFirst();
    	
    	int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
    	String filePath = cursor.getString(columnIndex);
    	cursor.close();
    	
    	this.updateImageView(BitmapFactory.decodeFile(filePath));
    }
    
    /**
     * Click Listener for selecting images from phone gallery
     * @author tscolari
     *
     */
    class ImagePickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			intent.setType("image/*");
			startActivityForResult(Intent.createChooser(intent, "Escolha uma Foto"), IMAGE_PICK);
			
		}
    }
    
    /**
     * Click listener for taking new picture
     * @author tscolari
     *
     */
    class TakePictureListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
			startActivityForResult(intent, IMAGE_CAPTURE);
			
		}
    }
}