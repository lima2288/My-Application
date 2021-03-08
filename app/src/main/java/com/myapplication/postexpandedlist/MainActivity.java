package com.myapplication.postexpandedlist;

import android.content.DialogInterface;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private List<Item> itemList;
    private Item selectedItem;
    private int lastSelectedPosition = 0;
    private boolean isRecentlyClosed = false;       //Flag for collapsed list item
    private RecyclerView recyclerView;
    private TextView textViewLabel;
    private ImageView imageViewLeft;
    private ImageView imageViewRight;
    private TextView postVal;
    private TextView commentsVal;
    private JSONArray keyArray;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        keyArray = new JSONArray();
        postVal=findViewById(R.id.postVal);
        commentsVal = (TextView) findViewById(R.id.commentsVal);
        try {
            getPosts(); // getting posts
            getComments(); // getting comments
        }
        catch(Exception e)
        {
            System.out.println("Exception"+e.getMessage());
        }
    }

    private void getPosts() {
        Call<List<Post>> call = RetrofitClient.getInstance().getMyApi().getPosts();
        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                List<Post> postList = response.body();

                //looping through all the posts and inserting the posts details inside the array
                for (int i = 0; i < postList.size(); i++) {
                    JSONObject obj = new JSONObject();
                    try {
                        obj.put("userid", postList.get(i).getUserId());
                        obj.put("id", postList.get(i).getId());
                        obj.put("title", postList.get(i).getTitle());
                        obj.put("body", postList.get(i).getBody());
                        keyArray.put(obj);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                postVal.setText(keyArray.toString());
            }
            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void getComments() {
        JSONObject objMainList = new JSONObject();
        JSONObject objMainList1 = new JSONObject();
        JSONArray arrayComment=new JSONArray();
        JSONArray itemArr=new JSONArray();
        Call<List<Comment>> call = RetrofitComments.getInstance().getMyApi().getComments();
        call.enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                List<Comment> commentList = response.body();
                try {
                    JSONArray array = new JSONArray(postVal.getText().toString());
                    for (int i = 0; i < array.length(); i++) {  //  looping through post array
                        JSONObject object = array.getJSONObject(i);
                        String id = object.getString("id");
                        // looping through comments array and checking the comments for a putricular post
                        for (int j = 0; j < commentList.size(); j++)
                        {
                            JSONObject obj = new JSONObject();
                            obj.put("id", commentList.get(j).getCommentId());
                            obj.put("title", commentList.get(j).getName());
                            obj.put("body", commentList.get(j).getBody());
                            obj.put("userid", commentList.get(j).getEmail());

                            if(commentList.get(j).getPostId().equals(id))
                            {
                                arrayComment.put(obj);
                            }
                        }
                        //comments appending to corresponding posts and set it to a text
                        object.put("item",arrayComment);
                        itemArr.put(object);
                        objMainList.put("item",itemArr);
                        objMainList1.put("postdetails",objMainList);
                        String s=objMainList1.toString();
                        commentsVal.setText(objMainList1.toString());
                    }
                    // Set data into view
                    setList();

                } catch (JSONException e) {
                    e.printStackTrace();
                    System.out.println("values error"+e.getMessage());
                }
            }
            @Override
            public void onFailure(Call<List<Comment>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void setList() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        textViewLabel = (TextView) findViewById(R.id.textViewLabel);
        imageViewLeft = (ImageView) findViewById(R.id.imageViewLeft);
        imageViewRight = (ImageView) findViewById(R.id.imageViewRight);
        final Item item = getItemList();
         if (item != null) {
            itemList = new ArrayList<>(item.getItemList());
            //Initial positioning
            selectedItem = itemList.get(0);
            itemList.get(0).setSelected(true);
            textViewLabel.setText(selectedItem.getId());
            imageViewLeft.setVisibility(View.GONE);
            ItemRWAdapter r=new ItemRWAdapter(this,itemList);

            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            //Divider for RecyclerView
            recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(),
                    DividerItemDecoration.VERTICAL));
            recyclerView.setAdapter(new ItemRWAdapter(this, itemList));

            imageViewLeft.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (lastSelectedPosition > 0) {
                        selectItem(lastSelectedPosition - 1, Const.NAV_LEFT);
                    }
                }
            });

            imageViewRight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (lastSelectedPosition < itemList.size() - 1) {
                        selectItem(lastSelectedPosition + 1, Const.NAV_RIGHT);
                    }
                }
            });
        } else {
             AlertDialog.Builder builder = new AlertDialog.Builder(this);
            final AlertDialog alertDialog = builder.create();
            alertDialog.setMessage("Empty List");
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    alertDialog.dismiss();
                    setList();
                }
            });
            alertDialog.show();
        }
    }

    /**
     * Controls the selected item and updates the interface
     *
     * @param position position of selected item (int)
     * @param navType  type of navigation source (int)
     */
    public void selectItem(int position, int navType) {
        //Checks whether the current selected item has children to expand/collapse
        if (navType == Const.NAV_RIGHT && itemList.get(position - 1).getItemList().size() > 0 &&
                !itemList.get(position - 1).isExpanded()) {
            position = position - 1;
        } else if (navType == Const.NAV_LEFT && itemList.get(position + 1).getItemList().size() > 0 &&
                itemList.get(position + 1).isExpanded()) {
            position = position + 1;
        }

        //Visibility of navigation buttons on top of screen
        imageViewLeft.setVisibility(position == 0 ? View.GONE : View.VISIBLE);
        imageViewRight.setVisibility(position == itemList.size() - 1 ? View.GONE : View.VISIBLE);

        isRecentlyClosed = false;
        selectedItem = itemList.get(position);
        itemList.get(position).setSelected(true);

        if (position != lastSelectedPosition) {
            itemList.get(lastSelectedPosition).setSelected(false);
        }
        lastSelectedPosition = position;

        //Delete children of collapsed list item from list
        if (selectedItem.getItemList().size() > 0) {
            if (selectedItem.isExpanded() && navType != Const.NAV_RIGHT) {
                deleteItemChildren(selectedItem, position);
            }
        }

        List<Item> updatedList = new ArrayList<>();

        //Add selected item and items till the selected item
        for (int i = 0; i < position + 1; i++) {
            updatedList.add(itemList.get(i));
        }

        //Add items if selected item has children
        if (selectedItem.getItemList().size() > 0 && !selectedItem.isExpanded() &&
                !isRecentlyClosed && navType != Const.NAV_LEFT) {
            updatedList.get(updatedList.size() - 1).setExpanded(true);
            for (int i = 0; i < selectedItem.getItemList().size(); i++) {
                Item item = selectedItem.getItemList().get(i);
                item.setExpanded(false);
                item.setHierarchy(selectedItem.getHierarchy() + 1);
                updatedList.add(item);
            }
        }

        //Add items coming after selected item
        for (int i = position + 1; i < itemList.size(); i++) {
            updatedList.add(itemList.get(i));
        }

        itemList = updatedList;
        recyclerView.setAdapter(new ItemRWAdapter(this, updatedList));
        if (navType != Const.NAV_BASIC) {
            recyclerView.getLayoutManager().scrollToPosition(position);
        }
    }

    /**
     * Gets JSON data from text and converts to Item object
     *
     * @return
     */
    private Item getItemList() {
        String json = "";
        JSONObject objTable = null;
        try {
            json= String.valueOf(commentsVal.getText());
            JSONObject obj = new JSONObject(json);
            objTable = obj.getJSONObject("postdetails");
          } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(
                    this, e.getMessage(), Toast.LENGTH_SHORT).show();
            return null;
        }

        return new Gson().fromJson(objTable.toString(), Item.class);
    }

    /**
     * Deletes all related children of selected parent recursively
     * in n-leveled parent-child list
     *
     * @param parentItem selected item
     * @param position   position of selected item
     */
    private void deleteItemChildren(Item parentItem, int position) {
        for (int i = 0; i < parentItem.getItemList().size(); i++) {
            Item innerItem = itemList.get(position + 1);
            itemList.remove(position + 1);

            if (innerItem.isExpanded() && innerItem.getItemList().size() > 0) {
                deleteItemChildren(innerItem, position);
            }
        }
        itemList.get(position).setExpanded(false);
        isRecentlyClosed = true;
    }
}

