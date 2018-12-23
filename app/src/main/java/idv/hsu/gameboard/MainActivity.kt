package idv.hsu.gameboard

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bt_start.setOnClickListener {
            if (et_column.text.toString() != "" && et_row.text.toString() != "" &&
                et_column.text.toString().toInt() > 0 && et_row.text.toString().toInt() > 0) {

                var i = Intent(this, GameboardActivity::class.java)
                i.putExtra(GameboardActivity.PARAM_ROW, et_row.text.toString().toInt())
                i.putExtra(GameboardActivity.PARAM_COL, et_column.text.toString().toInt())
                startActivity(i)

            } else {
                Toast.makeText(this, "Plz enter both nums and should bigger than 0.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}