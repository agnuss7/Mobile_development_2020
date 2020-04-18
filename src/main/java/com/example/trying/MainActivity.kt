package com.example.trying
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.icu.util.ValueIterator
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.TextView
import org.xml.sax.InputSource
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.File
import java.io.FileInputStream
import java.io.StringReader
import javax.xml.parsers.DocumentBuilderFactory
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.coroutines.coroutineContext
import kotlin.math.roundToInt


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
        }

        override fun onClick(p0: View?) {
            val int=Intent(context,QuestActivity::class.java).apply {
                putExtra("name", file)
            }
            context.startActivity(int)
        }

        override fun onLongClick(p0: View?): Boolean {
            //gal trint reikes

            return true
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
   // val ggg=File(applicationContext.filesDir,"/quests")
     //   ggg.mkdir()
        var file=File(applicationContext.filesDir,"/quests/second")
        file.writeText("""<name>thing</name>
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
        """.trimMargin())

        var dir=File(applicationContext.filesDir,"/quests")
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
            // l.add(Pair(o.name,0.25))
        }

        val questAdapter=QuestAdapter(this@MainActivity,l)
        var list:RecyclerView=findViewById(R.id.questList) as RecyclerView
   //     var list:RecyclerView=findViewById(R.id.questList) as RecyclerView

    // val list=findViewById(R.id.qu) as RecyclerView
        list.setHasFixedSize(true)
        list.layoutManager=LinearLayoutManager(this)
        list.adapter=questAdapter

       // list.addOnItemTouchListener()



    }

    override fun onRestart() {
        super.onRestart()
        finish()
        startActivity(getIntent())
    }



}