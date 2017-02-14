package tudolist.cursoandroid.com.tudolist;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends Activity {

    private EditText lblText;
    private Button btnAdicionar;
    private ListView listView;
    private SQLiteDatabase bancoDados;

    private ArrayAdapter<String> itensAdaptador;
    private ArrayList<String> itens;
    private ArrayList<Integer> ids;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            //Recuperar Componentes
            lblText = (EditText) findViewById(R.id.lblText);
            btnAdicionar = (Button) findViewById(R.id.btnAdicionar);
            //Lista
            listView = (ListView) findViewById(R.id.listView);


            //Banco de Dados
            bancoDados = openOrCreateDatabase("apptarefas", MODE_PRIVATE, null);

            //Tabela de Tarefas
            bancoDados.execSQL("CREATE TABLE IF NOT EXISTS tarefas(id INTEGER PRIMARY KEY AUTOINCREMENT, tarefa VARCHAR)");

            btnAdicionar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //Recuperando o texto digitado pelo usuário
                    String textoDigitado = lblText.getText().toString();
                    salvarTarefa(textoDigitado);


                }
            });

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    removerTarefa(ids.get(position));
                }
            });

            //Recuperar Tarefas
            recuperarTarefas();


        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void salvarTarefa(String texto){
        try{
            if(texto.isEmpty()){
                Toast.makeText(getApplicationContext(), "Não é permitido campos em branco", Toast.LENGTH_SHORT).show();
            }else{
                bancoDados.execSQL("INSERT INTO tarefas(tarefa) VALUES ('"+texto+"')");
                Toast.makeText(getApplicationContext(), "Tarefa agendada com Sucesso!", Toast.LENGTH_SHORT).show();
                recuperarTarefas();
                lblText.setText("");
            }

        }catch (Exception e){
            e.printStackTrace();
        }


    }

    private void recuperarTarefas(){
        try{
            //Recuperar as tarefas
            Cursor cursor = bancoDados.rawQuery("SELECT * FROM tarefas ORDER BY id DESC", null);


            //Recuperar os ids das colunas
            int indiceColunaId = cursor.getColumnIndex("id");
            int indiceColunaTarefa = cursor.getColumnIndex("tarefa");



            //Criar Adaptador
            itens = new ArrayList<String>();
            ids = new ArrayList<Integer>();
            itensAdaptador = new ArrayAdapter<String>(getApplicationContext(),
                    android.R.layout.simple_list_item_2,
                    android.R.id.text2,
                    itens);
            listView.setAdapter(itensAdaptador);


            //Listar as Tarefas
            cursor.moveToFirst();
            while(cursor != null){
                itens.add(cursor.getString(indiceColunaTarefa));
                ids.add(Integer.parseInt(cursor.getString(indiceColunaId)));
                cursor.moveToNext();
            }
        }catch (Exception e){
            e.printStackTrace();
        }



    }

    private void removerTarefa(Integer id){
        try{
            bancoDados.execSQL("DELETE FROM tarefas WHERE id="+id);
            Toast.makeText(getApplicationContext(), "Tarefa removida com sucesso!", Toast.LENGTH_SHORT).show();
            recuperarTarefas();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
