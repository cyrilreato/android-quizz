package ch.ifage.quizz.filesystem;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public final class FileHelper {

    private static FileHelper instance;
    Context context;
    private FileOutputStream outputStream;

    private FileHelper(Context context){
        this.context = context;
    }

    public static synchronized FileHelper getInstance(Context context){
        if(instance == null){
            instance = new FileHelper(context);
        }
        return instance;
    }

    public void listFiles(){

        File directory = new File(context.getFilesDir().getPath());
        // Listing files
        File[] fList = directory.listFiles();
        if(fList.length == 0){
            System.out.println("No file found !");
            return;
        }
        for(File file : fList) {
            System.out.println("File found: " + file.getAbsolutePath());
        }

    }

    public void deleteAllLocalFiles(){

        File directory = new File(context.getFilesDir().getPath());
        // Listing files
        File[] fList = directory.listFiles();
        for(File file : fList) {
            file.delete();
        }

    }

    public void showFreeSpace(){
        // Free space
        File directory = new File(context.getFilesDir().getPath());
        System.out.println(String.valueOf(directory.getFreeSpace()));
    }

    public void storeImage(String name, Bitmap bitmap) {

        try {
            FileOutputStream fos = context.openFileOutput(name, Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
