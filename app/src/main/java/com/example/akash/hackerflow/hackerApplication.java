package com.example.akash.hackerflow;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.View;

import com.github.dkharrat.nexusdialog.FormActivity;
import com.github.dkharrat.nexusdialog.controllers.EditTextController;
import com.github.dkharrat.nexusdialog.controllers.FormSectionController;
import com.github.dkharrat.nexusdialog.controllers.SelectionController;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.http.NextServiceFilterCallback;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilter;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterRequest;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.table.sync.MobileServiceSyncContext;
import com.microsoft.windowsazure.mobileservices.table.sync.localstore.ColumnDataType;
import com.microsoft.windowsazure.mobileservices.table.sync.localstore.MobileServiceLocalStoreException;
import com.microsoft.windowsazure.mobileservices.table.sync.localstore.SQLiteLocalStore;
import com.microsoft.windowsazure.mobileservices.table.sync.synchandler.SimpleSyncHandler;

import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class hackerApplication extends FormActivity {

    private MobileServiceClient mClient;
    ProgressDialog progressDialog;

    /**
     * Mobile Service Table used to access data
     */
    private MobileServiceTable<hackerDetails> mToDoTable;

    //Offline Sync
    /**
     * Mobile Service Table used to access and Sync data
     */
    /**
     * Initializes the activity
     */

    @Override
    protected void initForm() {
        try {
            // Create the Mobile Service Client instance, using the provided

            // Mobile Service URL and key
            mClient = new MobileServiceClient("https://hackerflow.azure-mobile.net/", "AZUREID",this).withFilter(new ProgressFilter());

            // Get the Mobile Service Table instance to use

            mToDoTable = mClient.getTable(hackerDetails.class);
            if(mToDoTable == null)
                Log.d("Error", "Null table");

            // Offline Sync
            //mToDoTable = mClient.getSyncTable("hackerDetails", hackerDetails.class);

            //Init local storage
            initLocalStore().get();

        } catch (MalformedURLException e) {
            createAndShowDialog(new Exception("There was an error creating the Mobile Service. Verify the URL"), "Error");
        } catch (Exception e){
            createAndShowDialog(e, "Error");
        }

        setTitle("Check-In");
        FormSectionController personalSection = new FormSectionController(this, "Personal Info");
        personalSection.addElement(new EditTextController(this, "firstName", "First name"));
        personalSection.addElement(new EditTextController(this, "lastName", "Last name"));
        personalSection.addElement(new EditTextController(this, "email", "Email ID"));
        personalSection.addElement(new EditTextController(this, "phone", "Phone Number"));
        personalSection.addElement(new SelectionController(this, "gender", "Gender", true, "Select", Arrays.asList("Male", "Female"), true));
        personalSection.addElement(new EditTextController(this, "age", "Age"));

        FormSectionController aboutYou = new FormSectionController(this, "About You");
        aboutYou.addElement(new EditTextController(this, "univ", "University"));
        aboutYou.addElement(new EditTextController(this, "year", "Graduation Year"));
        aboutYou.addElement(new EditTextController(this, "major", "Major"));
        aboutYou.addElement(new EditTextController(this, "hackNumber", "Number of hackathons attended"));
        aboutYou.addElement(new SelectionController(this, "teeSize", "T-Shirt Size", true, "Select", Arrays.asList("S", "M", "L", "XL"), true));
        aboutYou.addElement(new ButtonElement(this, "submit"));

        getFormController().addSection(personalSection);
        getFormController().addSection(aboutYou);
    }

    /**
     * Add a new item
     *
     * @param view
     *            The view that originated the call
     */
    public void addItem(View view) {

        if (mClient == null) {
            return;
        }

        // Create a new item
        final hackerDetails item = new hackerDetails();
        item.setFirstName(getModel().getValue("firstName").toString());
        item.setLastName(getModel().getValue("lastName").toString());
        item.setHackerGender(getModel().getValue("gender").toString());
        item.setHackerAge(getModel().getValue("age").toString());
        item.setHackerMail(getModel().getValue("email").toString());
        item.setHackerMajor(getModel().getValue("major").toString());
        item.setHackerUniv(getModel().getValue("univ").toString());
        item.setHackerShirtSize(getModel().getValue("teeSize").toString());
        item.setHackerPhone(getModel().getValue("phone").toString());
        item.setHackerYear(getModel().getValue("year").toString());
        item.setHackerNumHacks(getModel().getValue("hackNumber").toString());
        // Insert the new item
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
            hackerDetails entity;
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    entity = addItemInTable(item);
                    //Log.d("Entity", entity.getId());
                } catch (final Exception e) {
                    Log.d("Error", "Add Item");
                    createAndShowDialogFromTask(e, "Error");
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                Intent intent = new Intent(hackerApplication.this, hackerQR.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("hackerID", entity.getId());
                intent.putExtra("hackerPhone", entity.getHackerPhone());
                intent.putExtra("hackerName", entity.getFirstName());
                getApplicationContext().startActivity(intent);
            }
        };

        runAsyncTask(task);
    }

    /**
     * Add an item to the Mobile Service Table
     *
     * @param item
     *            The item to Add
     */
    public hackerDetails addItemInTable(hackerDetails item) throws ExecutionException, InterruptedException {
        hackerDetails entity = mToDoTable.insert(item).get();
        return entity;
    }

    /**
     * Refresh the list with the items in the Mobile Service Table
     */

    //Offline Sync
    /**
     * Refresh the list with the items in the Mobile Service Sync Table
     */
    /*private List<hackerDetails> refreshItemsFromMobileServiceTableSyncTable() throws ExecutionException, InterruptedException {
        //sync the data
        sync().get();
        Query query = QueryOperations.field("complete").
                eq(val(false));
        return mToDoTable.read(query).get();
    }*/

    /**
     * Initialize local storage
     * @return
     * @throws MobileServiceLocalStoreException
     * @throws ExecutionException
     * @throws InterruptedException
     */
    private AsyncTask<Void, Void, Void> initLocalStore() throws MobileServiceLocalStoreException, ExecutionException, InterruptedException {

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {

                    MobileServiceSyncContext syncContext = mClient.getSyncContext();

                    if (syncContext.isInitialized())
                        return null;

                    SQLiteLocalStore localStore = new SQLiteLocalStore(mClient.getContext(), "OfflineStore", null, 1);

                    Map<String, ColumnDataType> tableDefinition = new HashMap<String, ColumnDataType>();
                    tableDefinition.put("id", ColumnDataType.String);
                    tableDefinition.put("hackerFirstName", ColumnDataType.String);
                    tableDefinition.put("hackerLastName", ColumnDataType.String);
                    tableDefinition.put("hackerUniv", ColumnDataType.String);
                    tableDefinition.put("hackerGender", ColumnDataType.String);
                    tableDefinition.put("hackerAge", ColumnDataType.String);
                    tableDefinition.put("hackerYear", ColumnDataType.String);
                    tableDefinition.put("hackerMajor", ColumnDataType.String);
                    tableDefinition.put("hackerNumHacks", ColumnDataType.String);
                    tableDefinition.put("hackerShirtSize", ColumnDataType.String);
                    tableDefinition.put("hackerMail", ColumnDataType.String);
                    tableDefinition.put("hackerPhone", ColumnDataType.String);

                    localStore.defineTable("hackerDetails", tableDefinition);

                    SimpleSyncHandler handler = new SimpleSyncHandler();

                    syncContext.initialize(localStore, handler).get();

                } catch (final Exception e) {
                    Log.d("Error", "Local Init");
                    createAndShowDialogFromTask(e, "Error");
                }

                return null;
            }
        };

        return runAsyncTask(task);
    }

    //Offline Sync
    /**
     * Sync the current context and the Mobile Service Sync Table
     * @return
     */
    /*
    private AsyncTask<Void, Void, Void> sync() {
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    MobileServiceSyncContext syncContext = mClient.getSyncContext();
                    syncContext.push().get();
                    mToDoTable.pull(null).get();
                } catch (final Exception e) {
                    createAndShowDialogFromTask(e, "Error");
                }
                return null;
            }
        };
        return runAsyncTask(task);
    }
    */

    /**
     * Creates a dialog and shows it
     *
     * @param exception
     *            The exception to show in the dialog
     * @param title
     *            The dialog title
     */
    private void createAndShowDialogFromTask(final Exception exception, String title) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                createAndShowDialog(exception, "Error");
            }
        });
    }


    /**
     * Creates a dialog and shows it
     *
     * @param exception
     *            The exception to show in the dialog
     * @param title
     *            The dialog title
     */
    private void createAndShowDialog(Exception exception, String title) {
        Throwable ex = exception;
        if(exception.getCause() != null){
            ex = exception.getCause();
        }
        createAndShowDialog(ex.getMessage(), title);
    }

    /**
     * Creates a dialog and shows it
     *
     * @param message
     *            The dialog message
     * @param title
     *            The dialog title
     */
    private void createAndShowDialog(final String message, final String title) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(message);
        builder.setTitle(title);
        builder.create().show();
    }

    /**
     * Run an ASync task on the corresponding executor
     * @param task
     * @return
     */
    private AsyncTask<Void, Void, Void> runAsyncTask(AsyncTask<Void, Void, Void> task) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            return task.execute();
        }
    }

    private class ProgressFilter implements ServiceFilter {

        @Override
        public ListenableFuture<ServiceFilterResponse> handleRequest(ServiceFilterRequest request, NextServiceFilterCallback nextServiceFilterCallback) {

            final SettableFuture<ServiceFilterResponse> resultFuture = SettableFuture.create();


            runOnUiThread(new Runnable() {

                @Override
                public void run() {
//                    if (mProgressBar != null) mProgressBar.setVisibility(ProgressBar.VISIBLE);
                }
            });

            ListenableFuture<ServiceFilterResponse> future = nextServiceFilterCallback.onNext(request);

            Futures.addCallback(future, new FutureCallback<ServiceFilterResponse>() {
                @Override
                public void onFailure(Throwable e) {
                    resultFuture.setException(e);
                }

                @Override
                public void onSuccess(ServiceFilterResponse response) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            //
                            // if (mProgressBar != null) mProgressBar.setVisibility(ProgressBar.GONE);
                        }
                    });

                    resultFuture.set(response);
                }
            });

            return resultFuture;
        }
    }
}
