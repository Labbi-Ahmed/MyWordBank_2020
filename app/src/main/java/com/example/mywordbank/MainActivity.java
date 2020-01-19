package com.example.mywordbank;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;

import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    DatabaseHelper myDb;
    EditText editWord, editMeaming, id_forDelete;
    Button SubmitInDB;
    Button deletedButton;

    ListView listView;
    ArrayList<String> listItems;
    ArrayAdapter adapter;
    List<String> userSelection =  new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myDb = new DatabaseHelper(this);

        listItems = new ArrayList<>();

        editWord = (EditText) findViewById(R.id.inputWord);
        editMeaming = (EditText) findViewById(R.id.inputMeaning);
        SubmitInDB = (Button) findViewById(R.id.submitButton);
        listView = (ListView) findViewById(R.id.viewTheList);
        //deletedButton = (Button) findViewById(R.id.delet_buttom);
        //id_forDelete = (EditText) findViewById(R.id.id_for_delete);

        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(multiChoiceModeListener);



        AddData();
        viewData();
        //DeleteData();
        wordList();
    }

    private void wordList(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String text = listView.getItemAtPosition(position).toString();
                Toast.makeText(MainActivity.this,""+text, Toast.LENGTH_LONG).show();

            }
        });



    }



    private void viewData() {

        Cursor cursor = myDb.getAllData();
        if (cursor.getCount() == 0)
            Toast.makeText(MainActivity.this,"No Data to Show",Toast.LENGTH_SHORT).show();
        else {

            while (cursor.moveToNext()){
                listItems.add(cursor.getString(0)+"\n"+cursor.getString(1));
            }

            adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,listItems);
            listView.setAdapter(adapter);

        }
    }

    public  void  AddData(){
        SubmitInDB.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (!TextUtils.isEmpty(editWord.getText().toString()) && !TextUtils.isEmpty(editMeaming.getText().toString())){

                            if( checkMatch()){
                                boolean isInserted = myDb.insertData(editWord.getText().toString(),
                                        editMeaming.getText().toString());

                                if (isInserted){
                                    Toast.makeText(MainActivity.this, "Data inserted", Toast.LENGTH_SHORT).show();
                                    editWord.setText("");
                                    editMeaming.setText("");
                                    listItems.clear();
                                    viewData();
                                }
                                else
                                    Toast.makeText(MainActivity.this,"Data Not inserted",Toast.LENGTH_SHORT).show();
                            }

                            else

                                Toast.makeText(MainActivity.this,"Data Is Matched",Toast.LENGTH_SHORT).show();
                            }
                        else

                            Toast.makeText(MainActivity.this,"Data is Empty",Toast.LENGTH_SHORT).show();

                    }
                });
    }


    public boolean checkMatch(){

        Cursor cursor = myDb.getAllData();


        while (cursor.moveToNext()){

            //Toast.makeText(MainActivity.this,"  "+cursor.getString(1) ,Toast.LENGTH_SHORT).show();
            if(cursor.getString(1).toLowerCase().equals(editWord.getText().toString().toLowerCase()) )
                return  false;
        }

        return true;
    }


//    public void DeleteData(){
//
//        deletedButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Integer deletedRows = myDb.deleteData(id_forDelete.getText().toString());
//
//                if(deletedRows > 0) {
//                    Toast.makeText(MainActivity.this, "Deleted Data ", Toast.LENGTH_SHORT).show();
//                    id_forDelete.setText("");
//                    listItems.clear();
//                    viewData();
//                }
//                else
//                    Toast.makeText(MainActivity.this,"Deleted Not Data ",Toast.LENGTH_SHORT).show();
//            }
//        });
//
//
//
//    }


    public void removeItems(List<String> items){
        for (String item : items){
            Integer deletedRows = myDb.deleteData(FindWord(item));
            if(deletedRows > 0) {
                Toast.makeText(MainActivity.this, "Deleted Data ", Toast.LENGTH_SHORT).show();
                listItems.clear();
                viewData();

            }
            else
                Toast.makeText(MainActivity.this,"Deleted Not Data ",Toast.LENGTH_SHORT).show();


            //Toast.makeText(MainActivity.this, FindWord(item),Toast.LENGTH_SHORT).show();
        }
        //userSelection.clear();
    }

    String FindWord(String items){

        String[] arrOfItem = items.split("\n",2);
        return arrOfItem[0];
    }

    AbsListView.MultiChoiceModeListener multiChoiceModeListener = new AbsListView.MultiChoiceModeListener() {
        @Override
        public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {

            if(userSelection.contains(listItems.get(position))){
                userSelection.remove(listItems.get(position));
            }
            else
            {
                userSelection.add(listItems.get(position));


            }

            mode.setTitle(userSelection.size() + "items selected...");
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater menuInflater = mode.getMenuInflater();
            menuInflater.inflate(R.menu.context_menu,menu);

            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId())
            {
                case R.id.action_delete:
                    removeItems(userSelection);
                    mode.finish();
                    return true;

                    default:
                      return false;

            }

        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {

            userSelection.clear();
        }
    };


}
