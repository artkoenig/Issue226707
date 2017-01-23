package info.appcube.issue226707;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.support.v4.content.FileProvider.getUriForFile;

public class MainActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Button shareSim = (Button) findViewById(R.id.share_sim);
		Button shareRem = (Button) findViewById(R.id.share_rem);

		// get app file directories
		final File[] targets = ContextCompat.getExternalFilesDirs(this, null);
		if(targets.length < 2) {
			new AlertDialog.Builder(this)
					.setTitle("Wrong configuration")
					.setMessage("Please use a device with a removable SD card for testing")
					.create().show();
		} else {
			// write an example file
			// on the external storage (emulated SD card)
			createTestFileOnPath(targets[0]);
			// on the secondary external storage (removable SD card)
			createTestFileOnPath(targets[1]);

			shareSim.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					ShareCompat.IntentBuilder intentBuilder = ShareCompat.IntentBuilder.from(MainActivity.this);
					Uri uri = getUriForFile(
							MainActivity.this, "info.appcube.example", targets[0]);
					intentBuilder.addStream(uri);
					intentBuilder.setType("*/*");
					intentBuilder.startChooser();
				}
			});

			shareRem.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// this will fail, because there is no configuration option in "file_paths" for the FileProvider
					// to access this directory
					ShareCompat.IntentBuilder intentBuilder = ShareCompat.IntentBuilder.from(MainActivity.this);
					Uri uri = getUriForFile(
							MainActivity.this, "info.appcube.example", targets[1]);
					intentBuilder.addStream(uri);
					intentBuilder.setType("*/*");
					intentBuilder.startChooser();
				}
			});
		}
	}

	private void createTestFileOnPath(File path) {
		try {
			FileOutputStream fos = new FileOutputStream(path);
			fos.write(1);
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
