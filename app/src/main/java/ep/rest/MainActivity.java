package ep.rest;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements Callback<List<Book>> {
    private static final String TAG = MainActivity.class.getCanonicalName();

    private SwipeRefreshLayout swiper;
    private FloatingActionButton fabAdd;
    private RecyclerView rvBooks;
    private final List<Book> books = new ArrayList<>();
    private BookAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        adapter = new BookAdapter(this, books);
        adapter.setOnItemClickListener(new BookAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                final Intent intent = new Intent(MainActivity.this, BookDetailActivity.class);
                intent.putExtra("ep.rest.id", books.get(position).id);
                startActivity(intent);
            }
        });
        rvBooks = (RecyclerView) findViewById(R.id.items);
        rvBooks.setAdapter(adapter);
        rvBooks.setLayoutManager(new LinearLayoutManager(this));
        rvBooks.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        swiper = (SwipeRefreshLayout) findViewById(R.id.swiper);
        swiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                BookService.getInstance().getAll().enqueue(MainActivity.this);
            }
        });

        fabAdd = (FloatingActionButton) findViewById(R.id.fab_add);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, BookFormActivity.class));
            }
        });

        BookService.getInstance().getAll().enqueue(MainActivity.this);
    }

    @Override
    public void onResponse(Call<List<Book>> call, Response<List<Book>> response) {
        final List<Book> hits = response.body();

        if (response.isSuccessful()) {
            Log.i(TAG, "Hits: " + hits.size());
            books.clear();
            books.addAll(hits);
            adapter.notifyDataSetChanged();
        } else {
            String errorMessage;
            try {
                errorMessage = "An error occurred: " + response.errorBody().string();
            } catch (IOException e) {
                errorMessage = "An error occurred: error while decoding the error message.";
            }
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
            Log.e(TAG, errorMessage);
        }
        swiper.setRefreshing(false);
    }

    @Override
    public void onFailure(Call<List<Book>> call, Throwable t) {
        Log.w(TAG, "Error: " + t.getMessage(), t);
        swiper.setRefreshing(false);
    }
}
