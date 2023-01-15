import org.apache.log4j.helpers.OptionConverter
import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.sql.SparkSession
import java.io.PrintWriter
import java.io.File
import scala.io.Source

object data_Analyze_js{

	//提前定义case class Info,不可在def main里边定义，不然会报错，运行不了
	case class Info(userId: Long, itemId: Long, action: String, time: String)
    def main(args: Array[String]) {
        //屏蔽日志
        Logger.getLogger("org.apache.spark").setLevel(Level.WARN)
        Logger.getLogger("org.eclipse.jetty.server").setLevel(Level.OFF)        
		
		//使用本地模式启动spark-shell，使用2个CPU核心
        val conf = new SparkConf().setAppName("UserBehavior_Analyze").setMaster("local[2]")
       
	   //创建SparkContext和SparkSession对象
        val sc = new SparkContext(conf)
        val spark = SparkSession.builder().getOrCreate()

		//从分布式文件系统HDFS中加载数据，将RDD转换为DataFrame(利用反射机制推断RDD模式，提前定义case class Info)
		import spark.implicits._
		val inputFile = "hdfs://localhost:9000/dataset/Processed_UserBehavior.csv"
		val UserBehaviorDF = sc.textFile(inputFile).map(_.split(",")).map(attributes => Info(attributes(0).trim.toInt, attributes(1).trim.toInt, attributes(2), attributes(3))).toDF()

		//用户行为信息统计
		//val UserBehaviorDF1 = UserBehaviorDF
		//val UserBehaviorDF2 = if(UserBehaviorDF1("action") == "buy")UserBehaviorDF1("购买")
		val behavior_count = UserBehaviorDF.groupBy("action").count()	
        val result1 = behavior_count.toJSON.collectAsList.toString
        val writer1 = new PrintWriter(new File("/usr/local/hadoop/IdeaProjects/UserBehavior_Analyze/web/static/result1.json"))
        writer1.write(result1)
        writer1.close()  


		//销量前十的商品
		val top_10_item = UserBehaviorDF.filter(UserBehaviorDF("action") === "buy").select(UserBehaviorDF("itemId")).rdd.map(v => (v(0).toString,1)).reduceByKey(_+_).sortBy(_._2,false).take(10)
		val result2 = sc.parallelize(top_10_item).toDF.toJSON.collectAsList.toString
		val writer2 = new PrintWriter(new File("/usr/local/hadoop/IdeaProjects/UserBehavior_Analyze/web/static/result2.json"))
		writer2.write(result2)
        writer2.close() 
		
		//购物行为前十的用户
		val top_10_user = UserBehaviorDF.filter(UserBehaviorDF("action") === "buy").select(UserBehaviorDF("userId")).rdd.map(v => (v(0).toString, 1)).reduceByKey(_+_).sortBy(_._2, false).take(10)
		val result3 = sc.parallelize(top_10_user).toDF().toJSON.collectAsList.toString
		val writer3 = new PrintWriter(new File("/usr/local/hadoop/IdeaProjects/UserBehavior_Analyze/web/static/result3.json"))
		writer3.write(result3)
		writer3.close()
		
		//时间段内平台商品销量统计
		val buy_order_by_data = UserBehaviorDF.filter(UserBehaviorDF("action") === "buy").select(UserBehaviorDF("time")).rdd.map(v => (v.toString().replace("[", "").replace("]", "").split(" ")(0), 1)).reduceByKey(_+_).sortBy(_._1).collect()
		val result4 = sc.parallelize(buy_order_by_data).toDF().toJSON.collectAsList.toString
		val writer4 = new PrintWriter(new File("/usr/local/hadoop/IdeaProjects/UserBehavior_Analyze/web/static/result4.json"))
		writer4.write(result4)
		writer4.close()		
		
		
	}
    
}
