package idv.hsu.gameboard

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Point
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayout
import kotlinx.android.synthetic.main.activity_gameboard.*
import kotlin.concurrent.fixedRateTimer
import kotlin.random.Random

class GameboardActivity : AppCompatActivity() {

    companion object {
        const val PARAM_ROW = "row"
        const val PARAM_COL = "col"
        const val MSG_WHAT = 9527
        const val TYPE_BTN_GAME = 0
        const val TYPE_BTN_BOTTOM = 1
        const val TIMER = 1000L
    }

    private var row: Int = 0
    private var col: Int = 0

    private var size = Point()

    private var prePosition: Int = -1

    private var btnList: MutableList<Button> = mutableListOf()

    private var handler: Handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            when (msg?.what) {
                MSG_WHAT -> {
                    // 如果有前一個，那就恢復成預設
                    if (prePosition >= 0) {
                        setNormal((fl_gameboard.getChildAt(prePosition)) as Button, TYPE_BTN_GAME)
                        setNormal((fl_bottom.getChildAt(prePosition % col) as Button), TYPE_BTN_BOTTOM)
                    }
                    prePosition = msg.arg1

                    // 把 random 到的 cell 寫上 "random"，相對應底下的按鈕變色並且加上 onClickListener
                    (fl_gameboard.getChildAt(msg.arg1) as Button).setText(R.string.random)
                    setHighlight((fl_bottom.getChildAt(msg.arg1 % col) as Button), TYPE_BTN_BOTTOM)

                    // 遍歷所有 cell，把同一 column 的都變色，其餘全部初始化
                    for (x in 0..(fl_gameboard.childCount - 1)) {
                        when (x % col) {
                            (msg.arg1 % col) -> { // 把同一個 column 的按鈕變色
                                setHighlight((fl_gameboard.getChildAt(x) as Button), TYPE_BTN_GAME)
                            }
                            else -> { // 不是同一個 column 的按鈕恢復原來狀態
                                setNormal((fl_gameboard.getChildAt(x) as Button), TYPE_BTN_GAME)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setNormal(btn: Button, type: Int) {
        when (type) {
            TYPE_BTN_GAME -> {
                btn.setBackgroundResource(R.drawable.bg_game_item_normal)
                btn.text = ""
            }
            TYPE_BTN_BOTTOM -> {
                btn.setBackgroundResource(R.drawable.bg_game_bottom_normal)
                btn.setText(R.string.button)
                btn.setOnClickListener { }
            }
        }
    }

    private fun setHighlight(btn: Button, type: Int) {
        when (type) {
            TYPE_BTN_GAME -> {
                btn.setBackgroundResource(R.drawable.bg_game_item_highlight)
            }
            TYPE_BTN_BOTTOM -> {
                btn.setText(R.string.button)
                btn.setBackgroundResource(R.drawable.bg_game_item_highlight)
                btn.setOnClickListener {
                    for (x in 0..(fl_gameboard.childCount - 1)) {
                        setNormal(fl_gameboard.getChildAt(x) as Button, TYPE_BTN_GAME)
                    }
                    for (x in 0..(fl_bottom.childCount - 1)) {
                        setNormal(fl_bottom.getChildAt(x) as Button, TYPE_BTN_BOTTOM)
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gameboard)

        row = intent.extras?.getInt(PARAM_ROW) ?: 3
        col = intent.extras?.getInt(PARAM_COL) ?: 3

        var display = windowManager.defaultDisplay
        display.getSize(size)

        fl_bottom.flexDirection = FlexDirection.ROW
        for (x in 0..(col - 1)) {
            var btn = createBaseFlexItemTextView(this, getString(R.string.button), TYPE_BTN_BOTTOM)
            val lp = FlexboxLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            lp.flexGrow = 1.toFloat()
            btn.layoutParams = lp
            fl_bottom.addView(btn)
        }

        fl_gameboard.flexDirection = FlexDirection.ROW
        fl_gameboard.flexWrap = FlexWrap.WRAP
        for (x in 0..(row * col - 1)) {
            var btn = createBaseFlexItemTextView(this, "", TYPE_BTN_GAME)
            val lp = FlexboxLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            lp.width = size.x / col
            lp.flexGrow = 1.toFloat()
            btn.layoutParams = lp
            btnList.add(btn)
            fl_gameboard.addView(btn)
        }

        fixedRateTimer("default", false, 0L, TIMER) {
            val randomNum = Random.nextInt(0, btnList.size - 1)
            val message = Message()
            message.what = MSG_WHAT
            message.arg1 = randomNum
            handler.sendMessage(message)
        }
    }

    /**
     * 新增按鈕
     * @context Context
     * @word 要顯示在按鈕上的文字
     * @type 0 是遊戲格按鈕，1 是下面的按鈕
     */
    private fun createBaseFlexItemTextView(context: Context, word: String, type: Int): Button {
        return Button(context).apply {
            text = word
            gravity = Gravity.CENTER
            when (type) {
                TYPE_BTN_GAME -> {
                    setBackgroundResource(R.drawable.bg_game_item_normal)
                }
                TYPE_BTN_BOTTOM -> {
                    setBackgroundResource(R.drawable.bg_game_bottom_normal)
                }
            }
        }
    }
}
