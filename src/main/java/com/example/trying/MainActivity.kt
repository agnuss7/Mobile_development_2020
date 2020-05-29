package com.example.trying

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.Intent.getIntent
import android.content.Intent.getIntentOld
import android.os.Bundle
import android.os.Environment
import android.view.*
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.File
import kotlin.math.roundToInt


public fun showDialog(context: Context, f:String){
    lateinit var dialog:AlertDialog

    val builder = AlertDialog.Builder(context)

    builder.setTitle("Trynimas.")

    builder.setMessage(R.string.dialog_delete)

    val dialogClickListener = DialogInterface.OnClickListener{_,which ->
        when(which){
            DialogInterface.BUTTON_POSITIVE -> {
                val ggg = File(context?.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "/quests/"+f)
                if(ggg.exists()){
                ggg.delete()
                }
                val c=context as Activity
                dialog.cancel()
                c.finish()
                c.startActivity(c.getIntent())

            }
            DialogInterface.BUTTON_NEGATIVE -> dialog.cancel()
        }
    }

    builder.setPositiveButton("YES",dialogClickListener)

    builder.setNegativeButton("NO",dialogClickListener)

    dialog = builder.create()

    dialog.show()
}

class QuestAdapter(val context:Context,var dataList: ArrayList<Pair<Pair<String,String>,Double>>): RecyclerView.Adapter<RecyclerView.ViewHolder>()
{

    private inner class QuestViewHolder(itemView: View):RecyclerView.ViewHolder(itemView),View.OnClickListener,View.OnLongClickListener{
        var name:TextView
        var progres:ProgressBar
        var file:String=""
        init{
            name=itemView.findViewById(R.id.questName)
            progres=itemView.findViewById(R.id.progressBar)
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
        }
        override fun onLongClick(p0: View?): Boolean {

            showDialog(context,file)
            return true
        }
        override fun onClick(p0: View?) {
            val int=Intent(context,QuestActivity::class.java).apply {
                putExtra("name", file)
            }
            context.startActivity(int)
        }


        fun bind(pos: Int) {
            name.text=dataList[pos].first.second
            progres.setProgress((dataList[pos].second*100).roundToInt())
            file=dataList[pos].first.first
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup,viewType:Int): RecyclerView.ViewHolder {
        return QuestViewHolder(LayoutInflater.from(context).inflate(R.layout.quest_item, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            (holder as QuestViewHolder).bind(position)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun getItemViewType(position: Int): Int {
        return 0
    }



}
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val ggg = File(applicationContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "/quests")
        if (!ggg.exists()) {
            ggg.mkdir()
        var file = File(applicationContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "/quests/second.xml")
        file.writeText(
            """<name>thing</name>
            <count>2</count>
            <progress>0</progress>
            <quest>
            <clue>baduntss</clue>
            <key>a</key>
            </quest>
            <quest>
            <clue>漢字</clue>
            <key>a</key>
            </quest>
            <congrats>sveikiname</congrats>
        """.trimMargin()
        )
    }
        var dir=File(applicationContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),"/quests")
        var tenp=dir.listFiles()
        var l= arrayListOf<Pair<Pair<String,String>,Double>>()
        for(o in tenp){
            val str:String
            val count:Double
            val pro:Double
            val factory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = true
            val parser = factory.newPullParser()
            parser.setInput(o.inputStream(), null)
            parser.next()
            while((parser.eventType!=XmlPullParser.TEXT || parser.isWhitespace()) && parser.eventType!=XmlPullParser.END_DOCUMENT){
                parser.next()
            }
            str=parser.text
            parser.next()
            while((parser.eventType!=XmlPullParser.TEXT || parser.isWhitespace()) && parser.eventType!=XmlPullParser.END_DOCUMENT){
                parser.next()
            }
            count=parser.text.toDouble()
            parser.next()
            while((parser.eventType!=XmlPullParser.TEXT || parser.isWhitespace()) && parser.eventType!=XmlPullParser.END_DOCUMENT){
                parser.next()
            }
            pro=parser.text.toDouble()/count
            l.add(Pair(Pair(o.name,str),pro))
        }

        val questAdapter=QuestAdapter(this@MainActivity,l)
        var list:RecyclerView=findViewById(R.id.questList) as RecyclerView
        list.setLongClickable(true)

        list.setHasFixedSize(true)
        list.layoutManager=LinearLayoutManager(this)
        list.adapter=questAdapter



    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.title, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.to_shop -> {
            val inti=Intent(this,web::class.java)
            this.startActivity(inti)
            true
        }

        R.id.to_main -> {
            val inti=Intent(this,MainActivity::class.java)
            finish()
            this.startActivity(inti)
            true
        }

        else -> {
            super.onOptionsItemSelected(item)
        }
    }
    override fun onRestart() {
        super.onRestart()
        finish()
        startActivity(getIntent())
    }



}