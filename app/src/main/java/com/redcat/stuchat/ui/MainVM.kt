package com.redcat.stuchat.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.liuxe.lib_common.utils.LogUtils
import com.redcat.stuchat.app.AppConfig
import com.redcat.stuchat.app.RedCatApp
import com.redcat.stuchat.base.BaseViewModel
import com.redcat.stuchat.data.bean.RecordBean
import com.redcat.stuchat.data.bean.UserBean
import com.redcat.stuchat.data.bean.WordBean
import com.redcat.stuchat.data.room.entity.Record
import com.redcat.stuchat.utils.PrefUtils
import com.redcat.stuchat.utils.Preference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.reflect.Type

/**
 *  author : liuxe
 *  date : 2023/3/22 16:33
 *  description :
 */
class MainVM : BaseViewModel() {
    //本地数据
    private var isFirst: Boolean by Preference(Preference.isFirstOpen, true)
    private var wordsList: List<WordBean> by PrefUtils(PrefUtils.prefWordList, ArrayList())
    private var wordsIndex by PrefUtils(PrefUtils.prefWordIndex, 0)

    private var aqList: List<String> by PrefUtils(PrefUtils.prefaqList, ArrayList())
    private var aqIndex by PrefUtils(PrefUtils.prefaqIndex, 0)

    private var userData: UserBean by PrefUtils(PrefUtils.prefUser, UserBean())

    //模式 true 学习 false 闲聊
    var isLearnWord = false
    var isLearnWordData = MutableLiveData<Boolean>()

    //每页显示
    var pageLimit = 20

    //聊天记录
    var recordList = ArrayList<RecordBean>()
    var recordData = MutableLiveData<List<RecordBean>>()

    //是否需要移动到底部
    var scrollToBottom = MutableLiveData<Boolean>()

    //播放svga
    var playSvga = MutableLiveData<Int>()

    //进入房间
    var intoRoom = MutableLiveData<UserBean>()

    //初始化完成
    var initFinish = MutableLiveData<Long>()

    //时间戳显示
    var lastShowTimeStamp: Long = 0

    /**
     * 初始化用户数据
     * @return LiveData<UserBean>
     */
    fun initUser() = liveData<UserBean> {
        val user = UserBean(
            nickName = creatNickName(),//昵称
            avatar = creatAvatar(),//头像
            coin = 1000,//金币
            frame = 0,//头像框
            veh = 0,//座驾
            level = 1,//等级
            value = 0//经验值
        )
        userData = user
        LogUtils.e("" + user.toString())
        emit(user)
    }

    /**
     * 单词初始化
     * @return LiveData<String>
     */
    fun loadWord(fileName: String = "word.json") = liveData<String> {
        withContext(Dispatchers.IO) {
            // 解析Json数据
            val newstringBuilder = StringBuilder()
            var inputStream: InputStream? = null
            try {
                inputStream = RedCatApp.context.assets.open(fileName)
                val isr = InputStreamReader(inputStream)
                val reader = BufferedReader(isr)
                var jsonLine: String?
                while (reader.readLine().also { jsonLine = it } != null) {
                    newstringBuilder.append(jsonLine)
                }
                reader.close()
                isr.close()
                inputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            val str = newstringBuilder.toString()
            val gson = Gson()
            val wordType: Type = object : TypeToken<List<WordBean>>() {}.type
            wordsList = gson.fromJson(str, wordType)
            LogUtils.e("wordList:" + wordsList.size)

            val aq =
                "第一章　序abc我要给阿Q做正传，已经不止一两年了。但一面要做，一面又往回想，这足见我不是一个“立言”的人，因为从来不朽之笔，须传不朽之人，于是人以文传，文以人传——究竟谁靠谁传，渐渐的不甚了然起来，而终于归接到传阿Q，仿佛思想里有鬼似的。abc然而要做这一篇速朽的文章，才下笔，便感到万分的困难了。第一是文章的名目。孔子曰，“名不正则言不顺”。这原是应该极注意的。传的名目很繁多：列 传，自传，内传，外传，别传，家传，小传……，而可惜都不合。“列传”么，这一篇并非和许多阔人排在“正史”里；“自传”么，我又并非就是阿Q。说是 “外传”，“内传”在那里呢？倘用“内传”，阿Q又决不是神仙。“别传”呢，阿Q实在未曾有大总统上谕宣付国史馆立“本传”——虽说英国正史上并无“博 徒列传”，而文豪迭更司也做过《博徒别传》这一部书，但文豪则可，在我辈却不可。其次是“家传”，则我既不知与阿Q是否同宗，也未曾受他子孙的拜托；或 “小传”，则阿Q又更无别的“大传”了。总而言之，这一篇也便是“本传”，但从我的文章着想，因为文体卑下，是“引车卖浆者流”所用的话，所以不敢僭 称，便从不入三教九流的小说家所谓“闲话休题言归正传”这一句套话里，取出“正传”两个字来，作为名目，即使与古人所撰《书法正传》的“正传”字面上 很相混，也顾不得了。abc第二，立传的通例，开首大抵该是“某，字某，某地人也”，而我并不知道阿Q姓什么。有一回，他似乎是姓赵，但第二日便模糊了。那是赵太爷的儿子进了秀 才的时候，锣声镗镗的报到村里来，阿Q正喝了两碗黄酒，便手舞足蹈的说，这于他也很光采，因为他和赵太爷原来是本家，细细的排起来他还比秀才长三辈呢。其 时几个旁听人倒也肃然的有些起敬了。那知道第二天，地保便叫阿Q到赵太爷家里去；太爷一见，满脸溅朱，喝道：abc“阿Q，你这浑小子！你说我是你的本家么？”abc阿Q不开口。abc赵太爷愈看愈生气了，抢进几步说：“你敢胡说！我怎么会有你这样的本家？你姓赵么？”abc阿Q不开口，想往后退了；赵太爷跳过去，给了他一个嘴巴。abc“你怎么会姓赵！——你那里配姓赵！”abc阿Q并没有抗辩他确凿姓赵，只用手摸着左颊，和地保退出去了；外面又被地保训斥了一番，谢了地保二百文酒钱。知道的人都说阿Q太荒唐，自己去招打；他 大约未必姓赵，即使真姓赵，有赵太爷在这里，也不该如此胡说的。此后便再没有人提起他的氏族来，所以我终于不知道阿Q究竟什么姓。abc第三，我又不知道阿Q的名字是怎么写的。他活着的时候，人都叫他阿Quei，死了以后，便没有一个人再叫阿Quei了，那里还会有“著之竹帛”的 事。若论“著之竹帛”，这篇文章要算第一次，所以先遇着了这第一个难关。我曾仔细想：阿Quei，阿桂还是阿贵呢？倘使他号月亭，或者在八月间做过生日， 那一定是阿桂了；而他既没有号——也许有号，只是没有人知道他，——又未尝散过生日征文的帖子：写作阿桂，是武断的。又倘使他有一位老兄或令弟叫阿富，那 一定是阿贵了；而他又只是一个人：写作阿贵，也没有佐证的。其余音Quei的偏僻字样，更加凑不上了。先前，我也曾问过赵太爷的儿子茂才先生，谁料博雅 如此公，竟也茫然，但据结论说，是因为陈独秀办了《新青年》提倡洋字，所以国粹沦亡，无可查考了。我的最后的手段，只有托一个同乡去查阿Q犯事的案卷， 八个月之后才有回信，说案卷里并无与阿Quei的声音相近的人。我虽不知道是真没有，还是没有查，然而也再没有别的方法了。生怕注音字母还未通行，只好用 了“洋字”，照英国流行的拼法写他为阿Quei，略作阿Q。这近于盲从《新青年》，自己也很抱歉，但茂才公尚且不知，我还有什么好办法呢。abc第四，是阿Q的籍贯了。倘他姓赵，则据现在好称郡望的老例，可以照《郡名百家姓》上的注解，说是“陇西天水人也”，但可惜这姓是不甚可靠的，因此籍贯也就有些决不定。他虽然多住未庄，然而也常常宿在别处，不能说是未庄人，即使说是“未庄人也”，也仍然有乖史法的。abc我所聊以自|慰的，是还有一个“阿”字非常正确，绝无附会假借的缺点，颇可以就正于通人。至于其余，却都非浅学所能穿凿，只希望有“历史癖与考据癖”的胡适之先生的门人们，将来或者能够寻出许多新端绪来，但是我这《阿Q正传》到那时却又怕早经消灭了。abc以上可以算是序。abc第二章　优胜记略abc阿Q不独是姓名籍贯有些渺茫，连他先前的“行状”也渺茫。因为未庄的人们之于阿Q，只要他帮忙，只拿他玩笑，从来没有留心他的“行状”的。而阿Q自己也不说，独有和别人口角的时候，间或瞪着眼睛道：abc“我们先前——比你阔的多啦！你算是什么东西！”abc阿Q没有家，住在未庄的土谷祠里；也没有固定的职业，只给人家做短工，割麦便割麦，舂米便舂米，撑船便撑船。工作略长久时，他也或住在临时主人的家 里，但一完就走了。所以，人们忙碌的时候，也还记起阿Q来，然而记起的是做工，并不是“行状”；一闲空，连阿Q都早忘却，更不必说“行状”了。只是有一 回，有一个老头子颂扬说：“阿Q真能做！”这时阿Q赤着膊，懒洋洋的瘦伶仃的正在他面前，别人也摸不着这话是真心还是讥笑，然而阿Q很喜欢。abc阿Q又很自尊，所有未庄的居民，全不在他眼神里，甚而至于对于两位“文童”也有以为不值一笑的神情。夫文童者，将来恐怕要变秀才者也；赵太爷钱太爷 大受居民的尊敬，除有钱之外，就因为都是文童的爹爹，而阿Q在精神上独不表格外的崇奉，他想：我的儿子会阔得多啦！加以进了几回城，阿Q自然更自负，然而 他又很鄙薄城里人，譬如用三尺三寸宽的木板做成的凳子，未庄人叫“长凳”，他也叫“长凳”，城里人却叫“条凳”，他想：这是错的，可笑！油煎大头鱼，未庄 都加上半寸长的葱叶，城里却加上切细的葱丝，他想：这也是错的，可笑！然而未庄人真是不见世面的可笑的乡下人呵，他们没有见过城里的煎鱼！abc阿Q“先前阔”，见识高，而且“真能做”，本来几乎是一个“完人”了，但可惜他体质上还有一些缺点。最恼人的是在他头皮上，颇有几处不知于何时的癞疮 疤。这虽然也在他身上，而看阿Q的意思，倒也似乎以为不足贵的，因为他讳说“癞”以及一切近于“赖”的音，后来推而广之，“光”也讳，“亮”也讳，再后 来，连“灯”“烛”都讳了。一犯讳，不问有心与无心，阿Q便全疤通红的发起怒来，估量了对手，口讷的他便骂，气力小的他便打；然而不知怎么一回事，总还是 阿Q吃亏的时候多。于是他渐渐的变换了方针，大抵改为怒目而视了。abc谁知道阿Q采用怒目主义之后，未庄的闲人们便愈喜欢玩笑他。一见面，他们便假作吃惊的说：哙，亮起来了。”abc阿Q照例的发了怒，他怒目而视了。abc“原来有保险灯在这里！”他们并不怕。abc阿Q没有法，只得另外想出报复的话来：abc“你还不配……”这时候，又仿佛在他头上的是一种高尚的光容的癞头疮，并非平常的癞头疮了；但上文说过，阿Q是有见识的，他立刻知道和“犯忌”有点抵触，便不再往底下说。abc闲人还不完，只撩他，于是终而至于打。阿Q在形式上打败了，被人揪住黄辫子，在壁上碰了四五个响头，闲人这才心满意足的得胜的走了，阿Q站了一刻，心里想，“我总算被儿子打了，现在的世界真不像样……”于是也心满意足的得胜的走了。abc阿Q想在心里的，后来每每说出口来，所以凡是和阿Q玩笑的人们，几乎全知道他有这一种精神上的胜利法，此后每逢揪住他黄辫子的时候，人就先一着对他说：abc“阿Q，这不是儿子打老子，是人打畜生。自己说：人打畜生！”abc阿Q两只手都捏住了自己的辫根，歪着头，说道：abc“打虫豸，好不好？我是虫豸——还不放么？”abc但虽然是虫豸，闲人也并不放，仍旧在就近什么地方给他碰了五六个响头，这才心满意足的得胜的走了，他以为阿Q这回可遭了瘟。然而不到十秒钟，阿Q也心 满意足的得胜的走了，他觉得他是第一个能够自轻自贱的人，除了“自轻自贱”不算外，余下的就是“第一个”。状元不也是“第一个”么？“你算是什么东西” 呢！？abc阿Q以如是等等妙法克服怨敌之后，便愉快的跑到酒店里喝几碗酒，又和别人调笑一通，口角一通，又得了胜，愉快的回到土谷祠，放倒头睡着了。假使有钱，他便去押牌宝，一推人蹲在地面上，阿Q即汗流满面的夹在这中间，声音他最响：abc“青龙四百！”abc“咳……开……啦！”桩家揭开盒子盖，也是汗流满面的唱。“天门啦……角回啦……！人和穿堂空在那里啦……！阿Q的铜钱拿过来……！”abc“穿堂一百——一百五十！”abc阿Q的钱便在这样的歌吟之下，渐渐的输入别个汗流满面的人物的腰间。他终于只好挤出堆外，站在后面看，替别人着急，一直到散场，然后恋恋的回到土谷祠，第二天，肿着眼睛去工作。abc但真所谓“塞翁失马安知非福”罢，阿Q不幸而赢了一回，他倒几乎失败了。abc这是未庄赛神的晚上。这晚上照例有一台戏，戏台左近，也照例有许多的赌摊。做戏的锣鼓，在阿Q耳朵里仿佛在十里之外；他只听得桩家的歌唱了。他赢而又赢，铜钱变成角洋，角洋变成大洋，大洋又成了叠。他兴高采烈得非常：abc“天门两块！”abc他不知道谁和谁为什么打起架来了。骂声打声脚步声，昏头昏脑的一大阵，他才爬起来，赌摊不见了，人们也不见了，身上有几处很似乎有些痛，似乎也挨了几 拳几脚似的，几个人诧异的对他看。他如有所失的走进土谷祠，定一定神，知道他的一堆洋钱不见了。赶赛会的赌摊多不是本村人，还到那里去寻根柢呢？abc很白很亮的一堆洋钱！而且是他的——现在不见了！说是算被儿子拿去了罢，总还是忽忽不乐；说自己是虫豸罢，也还是忽忽不乐：他这回才有些感到失败的苦痛了。abc但他立刻转败为胜了。他擎起右手，用力的在自己脸上连打了两个嘴巴，热剌剌的有些痛；打完之后，便心平气和起来，似乎打的是自己，被打的是别一个自己，不久也就仿佛是自己打了别个一般，——虽然还有些热剌剌，——心满意足的得胜的躺下了。abc他睡着了。abc第三章　续优胜记略abc然而阿Q虽然常优胜，却直待蒙赵太爷打他嘴巴之后，这才出了名。abc他付过地保二百文酒钱，愤愤的躺下了，后来想：“现在的世界太不成话，儿子打老子……”于是忽而想到赵太爷的威风，而现在是他的儿子了，便自己也渐渐的得意起来，爬起身，唱着《小孤孀上坟》到酒店去。这时候，他又觉得赵太爷高人一等了。abc说也奇怪，从此之后，果然大家也仿佛格外尊敬他。这在阿Q，或者以为因为他是赵太爷的父亲，而其实也不然。未庄通例，倘如阿七打阿八，或者李四打张 三，向来本不算口碑。一上口碑，则打的既有名，被打的也就托庇有了名。至于错在阿Q，那自然是不必说。所以者何？就因为赵太爷是不会错的。但他既然错，为 什么大家又仿佛格外尊敬他呢？这可难解，穿凿起来说，或者因为阿Q说是赵太爷的本家，虽然挨了打，大家也还怕有些真，总不如尊敬一些稳当。否则，也如孔庙 里的太牢一般，虽然与猪羊一样，同是畜生，但既经圣人下箸，先儒们便不敢妄动了。abc阿Q此后倒得意了许多年。abc有一年的春天，他醉醺醺的在街上走，在墙根的日光下，看见王胡在那里赤着膊捉虱子，他忽然觉得身上也痒起来了。这王胡，又癞又胡，别人都叫他王癞胡， 阿Q却删去了一个癞字，然而非常渺视他。阿Q的意思，以为癞是不足为奇的，只有这一部络腮胡子，实在太新奇，令人看不上眼。他于是并排坐下去了。倘是别的 闲人们，阿Q本不敢大意坐下去。但这王胡旁边，他有什么怕呢？老实说：他肯坐下去，简直还是抬举他。abc阿Q也脱下破夹袄来，翻检了一回，不知道因为新洗呢还是因为粗心，许多工夫，只捉到三四个。他看那王胡，却是一个又一个，两个又三个，只放在嘴里毕毕剥剥的响。abc阿Q最初是失望，后来却不平了：看不上眼的王胡尚且那么多，自己倒反这样少，这是怎样的大失体统的事呵！他很想寻一两个大的，然而竟没有，好容易才捉到一个中的，恨恨的塞在厚嘴唇里，狠命一咬，劈的一声，又不及王胡的响。abc他癞疮疤块块通红了，将衣服摔在地上，吐一口唾沫，说：abc“这毛虫！”abc“癞皮狗，你骂谁？”王胡轻蔑的抬起眼来说。abc阿Q近来虽然比较的受人尊敬，自己也更高傲些，但和那些打惯的闲人们见面还胆怯，独有这回却非常武勇了。这样满脸胡子的东西，也敢出言无状么？abc“谁认便骂谁！”他站起来，两手叉在腰间说。abc“你的骨头痒了么？”王胡也站起来，披上衣服说。abc阿Q以为他要逃了，抢进去就是一拳。这拳头还未达到身上，已经被他抓住了，只一拉，阿Q跄跄踉踉的跌进去，立刻又被王胡扭住了辫子，要拉到墙上照例去碰头。abc“‘君子动口不动手’！”阿Q歪着头说。abc王胡似乎不是君子，并不理会，一连给他碰了五下，又用力的一推，至于阿Q跌出六尺多远，这才满足的去了。abc在阿Q的记忆上，这大约要算是生平第一件的屈辱，因为王胡以络腮胡子的缺点，向来只被他奚落，从没有奚落他，更不必说动手了。而他现在竟动手，很意外，难道真如市上所说，皇帝已经停了考，不要秀才和举人了，因此赵家减了威风，因此他们也便小觑了他么？abc阿Q无可适从的站着。abc远远的走来了一个人，他的对头又到了。这也是阿Q最厌恶的一个人，就是钱太爷的大儿子。他先前跑上城里去进洋学堂，不知怎么又跑到东洋去了，半年之后 他回到家里来，腿也直了，辫子也不见了，他的母亲大哭了十几场，他的老婆跳了三回井。后来，他的母亲到处说，“这辫子是被坏人灌醉了酒剪去了。本来可以做 大官，现在只好等留长再说了。”然而阿Q不肯信，偏称他“假洋鬼子”，也叫作“里通外国的人”，一见他，一定在肚子里暗暗的咒骂。abc阿Q尤其“深恶而痛绝之”的，是他的一条假辫子。辫子而至于假，就是没了做人的资格；他的老婆不跳第四回井，也不是好女人。abc这“假洋鬼子”近来了。abc秃儿。驴……”阿Q历来本只在肚子里骂，没有出过声，这回因为正气忿，因为要报仇，便不由的轻轻的说出来了。abc不料这秃儿却拿着一支黄漆的棍子——就是阿Q所谓哭丧棒——大蹋步走了过来。阿Q在这刹那，便知道大约要打了，赶紧抽紧筋骨，耸了肩膀等候着，果然，拍的一声，似乎确凿打在自己头上了。abc“我说他！”阿Q指着近旁的一个孩子，分辩说。abc拍！拍拍！abc在阿Q的记忆上，这大约要算是生平第二件的屈辱。幸而拍拍的响了之后，于他倒似乎完结了一件事，反而觉得轻松些，而且“忘却”这一件祖传的宝贝也发生了效力，他慢慢的走，将到酒店门口，早已有些高兴了。abc但对面走来了静修庵里的小尼姑。阿Q便在平时，看见伊也一定要唾骂，而况在屈辱之后呢？他于是发生了回忆，又发生了敌忾了。abc“我不知道我今天为什么这样晦气，原来就因为见了你！”他想。abc他迎上去，大声的吐一口唾沫：abc“咳，呸！”abc小尼姑全不睬，低了头只是走。阿Q走近伊身旁，突然伸出手去摩着伊新剃的头皮，呆笑着，说：abc“秃儿！快回去，和尚等着你……”abc“你怎么动手动脚……”尼姑满脸通红的说，一面赶快走。abc酒店里的人大笑了。阿Q看见自己的勋业得了赏识，便愈加兴高采烈起来：abc“和尚动得，我动不得？”他扭住伊的面颊。abc酒店里的人大笑了。阿Q更得意，而且为了满足那些赏鉴家起见，再用力的一拧，才放手。abc他这一战，早忘却了王胡，也忘却了假洋鬼子，似乎对于今天的一切“晦气”都报了仇；而且奇怪，又仿佛全身比拍拍的响了之后轻松，飘飘然的似乎要飞去了。abc“这断子绝孙的阿Q！”远远地听得小尼姑的带哭的声音。abc“哈哈哈！”阿Q十分得意的笑。abc“哈哈哈！”酒店里的人也九分得意的笑。abc第四章　恋爱的悲剧abc有人说：有些胜利者，愿意敌手如虎，如鹰，他才感得胜利的欢喜；假使如羊，如小鸡，他便反觉得胜利的无聊。又有些胜利者，当克服一切之后，看见死的死 了，降的降了，“臣诚惶诚恐死罪死罪”，他于是没有了敌人，没有了对手，没有了朋友，只有自己在上，一个，孤另另，凄凉，寂寞，便反而感到了胜利的悲哀。然而我们的阿Q却没有这样乏，他是永远得意的：这或者也是中国精神文明冠于全球的一个证据了。abc看哪，他飘飘然的似乎要飞去了！abc然而这一次的胜利，却又使他有些异样。他飘飘然的飞了大半天，飘进土谷祠，照例应该躺下便打鼾。谁知道这一晚，他很不容易合眼，他觉得自己的大拇指和 第二指有点古怪：仿佛比平常滑腻些。不知道是小尼姑的脸上有一点滑腻的东西粘在他指上，还是他的指头在小尼姑脸上磨得滑腻了？……abc“断子绝孙的阿Q！”abc阿Q的耳朵里又听到这句话。他想：不错，应该有一个女人，断子绝孙便没有人供一碗饭，……应该有一个女人。夫“不孝有三无后为大”，而“若敖之鬼馁而”，也是一件人生的大哀，所以他那思想，其实是样样合于圣经贤传的，只可惜后来有些“不能收其放心”了。abc“女人，女人！……”他想。abc“……和尚动得……女人，女人！……女人！”他又想。abc我们不能知道这晚上阿Q在什么时候才打鼾。但大约他从此总觉得指头有些滑腻，所以他从此总有些飘飘然；“女……”他想。abc即此一端，我们便可以知道女人是害人的东西。abc中国的男人，本来大半都可以做圣贤，可惜全被女人毁掉了。商是妲己闹亡的；周是褒姒弄坏的；秦……虽然史无明文，我们也假定他因为女人，大约未必十分错；而董卓可是的确给貂蝉害死了。abc阿Q本来也是正人，我们虽然不知道他曾蒙什么明师指授过，但他对于“男女之大防”㈠却历来非常严；也很有排斥异端——如小尼姑及假洋鬼子之类——的正 气。他的学说是：凡尼姑，一定与和尚私通；一个女人在外面走，一定想引诱野男人；一男一女在那里讲话，一定要有勾当了。为惩治他们起见，所以他往往怒目而 视，或者大声说几句“诛心”㈡话，或者在冷僻处，便从后面掷一块小石头。abc谁知道他将到“而立”㈢之年，竟被小尼姑害得飘飘然了。这飘飘然的精神，在礼教上是不应该有的，——所以女人真可恶，假使小尼姑的脸上不滑腻，阿Q便 不至于被蛊，又假使小尼姑的脸上盖一层布，阿Q便也不至于被蛊了，——他五六年前，曾在戏台下的人丛中拧过一个女人的大腿，但因为隔一层裤，所以此后并不 飘飘然，——而小尼姑并不然，这也足见异端之可恶。abc“女……”阿Q想。abc他对于以为“一定想引诱野男人”的女人，时常留心看，然而伊并不对他笑。他对于和他讲话的女人，也时常留心听，然而伊又并不提起关于什么勾当的话来。哦，这也是女人可恶之一节：伊们全都要装“假正经”的。abc这一天，阿Q在赵太爷家里舂了一天米，吃过晚饭，便坐在厨房里吸旱烟。倘在别家，吃过晚饭本可以回去的了，但赵府上晚饭早，虽说定例不准掌灯，一吃完 便睡觉，然而偶然也有一些例外：其一，是赵大爷未进秀才的时候，准其点灯读文章；其二，便是阿Q来做短工的时候，准其点灯舂米。因为这一条例外，所以阿Q 在动手舂米之前，还坐在厨房里吸烟旱。abc吴妈，是赵太爷家里唯一的女仆，洗完了碗碟，也就在长凳上坐下了，而且和阿Q谈闲天：abc“太太两天没有吃饭哩，因为老爷要买一个小的……”abc“女人……吴妈……这小孤孀……”阿Q想。abc“我们的少奶奶是八月里要生孩子了……”abc女人……”阿Q想。abc阿Q放下烟管，站了起来。abc“我们的少奶奶……”吴妈还唠叨说。abc“我和你困觉，我和你困觉！”阿Q忽然抢上去，对伊跪下了。abc一刹时中很寂然。abc“阿呀！”吴妈楞了一息，突然发抖，大叫着往外跑，且跑且嚷，似乎后来带哭了。abc阿Q对了墙壁跪着也发楞，于是两手扶着空板凳，慢慢的站起来，仿佛觉得有些糟。他这时确也有些忐忑了，慌张的将烟管插在裤带上，就想去舂米。蓬的一声，头上着了很粗的一下，他急忙回转身去，那秀才便拿了一支大竹杠站在他面前。abc“你反了，……你这……”abc大竹杠又向他劈下来了。阿Q两手去抱头，拍的正打在指节上，这可很有些痛。他冲出厨房门，仿佛背上又着了一下似的。abc“忘八蛋！”秀才在后面用了官话这样骂。abc阿Q奔入舂米场，一个人站着，还觉得指头痛，还记得“忘八蛋”，因为这话是未庄的乡下人从来不用，专是见过官府的阔人用的，所以格外怕，而印象也格外 深。但这时，他那“女……”的思想却也没有了。而且打骂之后，似乎一件事也已经收束，倒反觉得一无挂碍似的，便动手去舂米。舂了一会，他热起来了，又歇了 手脱衣服。abc脱下衣服的时候，他听得外面很热闹，阿Q生平本来最爱看热闹，便即寻声走出去了。寻声渐渐的寻到赵太爷的内院里，虽然在昏黄中，却辨得出许多人，赵府一家连两日不吃饭的太太也在内，还有间壁的邹七嫂，真正本家的赵白眼，赵司晨。abc少奶奶正拖着吴妈走出下房来，一面说：abc“你到外面来，……不要躲在自己房里想……”abc“谁不知道你正经，……短见是万万寻不得的。”邹七嫂也从旁说。abc吴妈只是哭，夹些话，却不甚听得分明。abc阿Q想：“哼，有趣，这小孤孀不知道闹着什么玩意儿了？”他想打听，走近赵司晨的身边。这时他猛然间看见赵大爷向他奔来，而且手里捏着一支大竹杠。他 看见这一支大竹杠，便猛然间悟到自己曾经被打，和这一场热闹似乎有点相关。他翻身便走，想逃回舂米场，不图这支竹杠阻了他的去路，于是他又翻身便走，自然 而然的走出后门，不多工夫，已在土谷祠内了。abc阿Q坐了一会，皮肤有些起粟，他觉得冷了，因为虽在春季，而夜间颇有余寒，尚不宜于赤膊。他也记得布衫留在赵家，但倘若去取，又深怕秀才的竹杠。然而地保进来了。abc“阿Q，你的妈妈的！你连赵家的用人都调戏起来，简直是造反。害得我晚上没有觉睡，你的妈妈的！……”abc如是云云的教训了一通，阿Q自然没有话。临末，因为在晚上，应该送地保加倍酒钱四百文，Q正没有现钱，便用一顶毡帽做抵押，并且订定了五条件：abc一明天用红烛——要一斤重的——一对，香一封，到赵府上去赔罪。abc二赵府上请道士祓除缢鬼，费用由阿Q负担。abc三阿Q从此不准踏进赵府的门槛。abc四吴妈此后倘有不测，惟阿Q是问。abc五阿Q不准再去索取工钱和布衫。abc阿Q自然都答应了，可惜没有钱。幸而已经春天，棉被可以无用，便质了二千大钱，履行条约。赤膊磕头之后，居然还剩几文，他也不再赎毡帽，统统喝了酒 了。但赵家也并不烧香点烛，因为太太拜佛的时候可以用，留着了。那破布衫是大半做了少奶奶八月间生下来的孩子的衬尿布，那小半破烂的便都做了吴妈的鞋底。abc第五章　生计问题abc阿Q礼毕之后，仍旧回到土谷祠，太陽下去了，渐渐觉得世上有些古怪。他仔细一想，终于省悟过来：其原因盖在自己的赤膊。他记得破夹袄还在，便披在身上，躺倒了，待张开眼睛，原来太陽又已经照在西墙上头了。他坐起身，一面说道，“妈妈的……”abc他起来之后，也仍旧在街上逛，虽然不比赤膊之有切肤之痛，却又渐渐的觉得世上有些古怪了。仿佛从这一天起，未庄的女人们忽然都怕了羞，伊们一见阿Q走 来，便个个躲进门里去。甚而至于将近五十岁的邹七嫂，也跟着别人乱钻，而且将十一的女儿都叫进去了。阿Q很以为奇，而且想：“这些东西忽然都学起小姐模样 来了。这娼妇们……”abc但他更觉得世上有些古怪，却是许多日以后的事。其一，酒店不肯赊欠了；其二，管土谷祠的老头子说些废话，似乎叫他走；其三，他虽然记不清多少日，但确 乎有许多日，没有一个人来叫他做短工。酒店不赊，熬着也罢了；老头子催他走，噜苏一通也就算了；只是没有人来叫他做短工，却使阿Q肚子饿：这委实是一件非 常“妈妈的”的事情。abc阿Q忍不下去了，他只好到老主顾的家里去探问，——但独不许踏进赵府的门槛，——然而情形也异样：一定走出一个男人来，现了十分烦厌的相貌，像回复乞丐一般的摇手道：abc“没有没有！你出去！”abc阿Q愈觉得稀奇了。他想，这些人家向来少不了要帮忙，不至于现在忽然都无事，这总该有些蹊跷在里面了。他留心打听，才知道他们有事都去叫小Don㈣。这小D，是一个穷小子，又瘦又乏，在阿Q的眼睛里，位置是在王胡之下的，谁料这小子竟谋了他的饭碗去。所以阿Q这一气，更与平常不同，当气愤愤的走着的时 候，忽然将手一扬，唱道：abc“我手执钢鞭将你打！㈤……”abc几天之后，他竟在钱府的照壁前遇见了小D。“仇人相见分外眼明”，阿Q便迎上去，小D也站住了。abc“畜生！”阿Q怒目而视的说，嘴角上飞出唾沫来。abc“我是虫豸，好么？……”小D说。abc这谦逊反使阿Q更加愤怒起来，但他手里没有钢鞭，于是只得扑上去，伸手去拔小D的辫子。小D一手护住了自己的辫根，一手也来拔阿Q的辫子，阿Q便也将 空着的一只手护住了自己的辫根。从先前的阿Q看来，，小D本来是不足齿数的，但他近来挨了饿，又瘦又乏已经不下于小D，所以便成了势均力敌的现象，四只手 拔着两颗头，都弯了腰，在钱家粉墙上映出一个蓝色*的虹形，至于半点钟之久了。abc“好了，好了！”看的人们说，大约是解劝的。abc“好，好！”看的人们说，不知道是解劝，是颂扬，还是煽动。abc然而他们都不听。阿Q进三步，小D便退三步，都站着；小D进三步，阿Q便退三步，又都站着。大约半点钟，——未庄少有自鸣钟，所以很难说，或者二十 分，——他们的头发里便都冒烟，额上便都流汗，阿Q的手放松了，在同一瞬间，小D的手也正放松了，同时直起，同时退开，都挤出人丛去。abc“记着罢，妈妈的……”阿Q回过头去说。abc“妈妈的，记着罢……”小D也回过头来说。abc这一场“龙虎斗”似乎并无胜败，也不知道看的人可满足，都没有发什么议论，而阿Q 却仍然没有人来叫他做短工。abc有一日很温和，微风拂拂的颇有些夏意了，阿Q却觉得寒冷起来，但这还可担当，第一倒是肚子饿。棉被，毡帽，布衫，早已没有了，其次就卖了棉袄；现在有 裤子，却万不可脱的；有破夹袄，又除了送人做鞋底之外，决定卖不出钱。他早想在路上拾得一注钱，但至今还没有见；他想在自己的破屋里忽然寻到一注钱，慌张 的四顾，但屋内是空虚而且了然。于是他决计出门求食去了。abc他在路上走着要“求食”，看见熟识的酒店，看见熟识的馒头，但他都走过了，不但没有暂停，而且并不想要。他所求的不是这类东西了；他求的是什么东西，他自己不知道。abc未庄本不是大村镇，不多时便走尽了。村外多是水田，满眼是新秧的嫩绿，夹着几个圆形的活动的黑点，便是耕田的农夫。阿Q并不赏鉴这田家乐，却只是走，因为他直觉的知道这与他的“求食”之道是很辽远的。但他终于走到静修庵的墙外了。abc庵周围也是水田，粉墙突出在新绿里，后面的低土墙里是菜园。阿Q迟疑了一会，四面一看，并没有人。他便爬上这矮墙去，扯着何首乌藤，但泥土仍然簌簌的 掉，阿Q的脚也索索的抖；终于攀着桑树枝，跳到里面了。里面真是郁郁葱葱，但似乎并没有黄酒馒头，以及此外可吃的之类。靠西墙是竹丛，下面许多笋，只可惜 都是并未煮熟的，还有油菜早经结子，芥菜已将开花，小白菜也很老了。abc阿Q仿佛文童落第似的觉得很冤屈，他慢慢走近园门去，忽而非常惊喜了，这分明是一畦老萝卜。他于是蹲下便拔，而门口突然伸出一个很圆的头来，又即缩回 去了，这分明是小尼姑。小尼姑之流是阿Q本来视若草芥的，但世事须“退一步想”，所以他便赶紧拔起四个萝卜，拧下青叶，兜在大襟里。然而老尼姑已经出来 了。abc“阿弥陀佛，阿Q，你怎么跳进园里来偷萝卜！……阿呀，罪过呵，阿唷，阿弥陀佛！……”abc“我什么时候跳进你的园里来偷萝卜？”阿Q且看且走的说。abc“现在……这不是？”老尼姑指着他的衣兜。abc“这是你的？你能叫得他答应你么？你……”abc阿Q没有说完话，拔步便跑；追来的是一匹很肥大的黑狗。这本来在前门的，不知怎的到后园来了。黑狗哼而且追，已经要咬着阿Q的腿，幸而从衣兜里落下一 个萝卜来，那狗给一吓，略略一停，阿Q已经爬上桑树，跨到土墙，连人和萝卜都滚出墙外面了。只剩着黑狗还在对着桑树嗥，老尼姑念着佛。abc阿Q怕尼姑又放出黑狗来，拾起萝卜便走，沿路又捡了几块小石头，但黑狗却并不再现。阿Q于是抛了石块，一面走一面吃，而且想道，这里也没有什么东西寻，不如进城去……abc待三个萝卜吃完时，他已经打定了进城的主意了。abc第六章　从中兴到末路abc在未庄再看见阿Q出现的时候，是刚过了这年的中秋。人们都惊异，说是阿Q回来了，于是又回上去想道，他先前那里去了呢？阿Q前几回的上城，大抵早就兴 高采烈的对人说，但这一次却并不，所以也没有一个人留心到。他或者也曾告诉过管土谷祠的老头子，然而未庄老例，只有赵太爷钱太爷和秀才大爷上城才算一件 事。假洋鬼子尚且不足数，何况是阿Q：因此老头子也就不替他宣传，而未庄的社会上也就无从知道了。abc但阿Q这回的回来，却与先前大不同，确乎很值得惊异。天色*将黑，他睡眼蒙胧的在酒店门前出现了，他走近柜台，从腰间伸出手来，满把是银的和铜的，在柜 上一扔说，“现钱！打酒来！”穿的是新夹袄，看去腰间还挂着一个大搭连，沉钿钿的将裤带坠成了很弯很弯的弧线。未庄老例，看见略有些醒目的人物，是与其慢 也宁敬的，现在虽然明知道是阿Q，但因为和破夹袄的阿Q有些两样了，古人云，“士别三日便当刮目相待”㈥，所以堂倌，掌柜，酒客，路人，便自然显出一种凝 而且敬的形态来。掌柜既先之以点头，又继之以谈话：abc“豁，阿Q，你回来了！”abc“回来了。”abc“发财发财，你是——在……”abc“上城去了！”abc这一件新闻，第二天便传遍了全未庄。人人都愿意知道现钱和新夹袄的阿Q的中兴史，所以在酒店里，茶馆里，庙檐下，便渐渐的探听出来了。这结果，是阿Q得了新敬畏。abc据阿Q说，他是在举人老爷家里帮忙。这一节，听的人都肃然了。这老爷本姓白，但因为合城里只有他一个举人，所以不必再冠姓，说起举人来就是他。这也不 独在未庄是如此，便是一百里方圆之内也都如此，人们几乎多以为他的姓名就叫举人老爷的了。在这人的府上帮忙，那当然是可敬的。但据阿Q又说，他却不高兴再 帮忙了，因为这举人老爷实在太“妈妈的”了。这一节，听的人都叹息而且快意，因为阿Q本不配在举人老爷家里帮忙，而不帮忙是可惜的。abc据阿Q说，他的回来，似乎也由于不满意城里人，这就在他们将长凳称为条凳，而且煎鱼用葱丝，加以最近观察所得的缺点，是女人的走路也扭得不很好。然而 也偶有大可佩服的地方，即如未庄的乡下人不过打三十二张的竹牌㈦，只有假洋鬼子能够叉“麻酱”，城里却连小乌龟子都叉得精熟的。什么假洋鬼子，只要放在城 里的十几岁的小乌龟子的手里，也就立刻是“小鬼见阎王”。这一节，听的人都赧然了。abc“你们可看见过杀头么？”阿Q说，“咳，好看。杀革命党。唉，好看好看，……”他摇摇头，将唾沫飞在正对面的赵司晨的脸上。这一节，听的人都凛然了。但阿Q又四面一看，忽然扬起右手，照着伸长脖子听得出神的王胡的后项窝上直劈下去道：abc“嚓！”abc王胡惊得一跳，同时电光石火似的赶快缩了头，而听的人又都悚然而且欣然了。从此王胡瘟头瘟脑的许多日，并且再不敢走近阿Q的身边；别的人也一样。abc阿Q这时在未庄人眼睛里的地位，虽不敢说超过赵太爷，但谓之差不多，大约也就没有什么语病的了。abc然而不多久，这阿Q的大名忽又传遍了未庄的闺中。虽然未庄只有钱赵两姓是大屋，此外十之九都是浅闺，但闺中究竟是闺中，所以也算得一件神异。女人们见 面时一定说，邹七嫂在阿Q那里买了一条蓝绸裙，旧固然是旧的，但只化了九角钱。还有赵白眼的母亲，——一说是赵司晨的母亲，待考，——也买了一件孩子穿的 大红洋纱衫，七成新，只用三百大钱九二串㈧。于是伊们都眼巴巴的想见阿Q，缺绸裙的想问他买绸裙，要洋纱衫的想问他买洋纱衫，不但见了不逃避，有时阿Q已 经走过了，也还要追上去叫住他，问道：abc“阿Q，你还有绸裙么？没有？纱衫也要的，有罢？”abc后来这终于从浅闺传进深闺里去了。因为邹七嫂得意之余，将伊的绸裙请赵太太去鉴赏，赵太太又告诉了赵太爷而且着实恭维了一番。赵太爷便在晚饭桌上，和 秀才大爷讨论，以为阿Q实在有些古怪，我们门窗应该小心些；但他的东西，不知道可还有什么可买，也许有点好东西罢。加以赵太太也正想买一件价廉物美的皮背 心。于是家族决议，便托邹七嫂即刻去寻阿Q，而且为此新辟了第三种的例外：这晚上也姑且特准点油灯。abc油灯干了不少了，阿Q还不到。赵府的全眷都很焦急，打着呵欠，或恨阿Q太飘忽，或怨邹七嫂不上紧。赵太太还怕他因为春天的条件不敢来，而赵太爷以为不足虑：因为这是“我”去叫他的。果然，到底赵太爷有见识，阿Q终于跟着邹七嫂进来了。abc“他只说没有没有，我说你自己当面说去，他还要说，我说……”邹七嫂气喘吁吁的走着说。abc“太爷！”阿Q似笑非笑的叫了一声，在檐下站住了。abc“阿Q，听说你在外面发财，”赵太爷踱开去，眼睛打量着他的全身，一面说。“那很好，那很好的。这个，……听说你有些旧东西，……可以都拿来看一看，……这也并不是别的，因为我倒要……”abc“我对邹七嫂说过了。都完了。”abc“完了？”赵太爷不觉失声的说，“那里会完得这样快呢？”abc“那是朋友的，本来不多。他们买了些，……”abc“总该还有一点罢。”abc“现在，只剩了一张门幕了。”abc“就拿门幕来看看罢。”赵太太慌忙说。abc“那么，明天拿来就是，”赵太爷却不甚热心了。“阿Q，你以后有什么东西的时候，你尽先送来给我们看，……”abc“价钱决不会比别家出得少！”秀才说。秀才娘子忙一瞥阿Q的脸，看他感动了没有。abc“我要一件皮背心。”赵太太说。abc阿Q虽然答应着，却懒洋洋的出去了，也不知道他是否放在心上。这使赵太爷很失望，气愤而且担心，至于停止了打呵欠。秀才对于阿Q的态度也很不平，于是 说，这忘八蛋要提防，或者不如吩咐地保，不许他住在未庄。但赵太爷以为不然，说这也怕要结怨，况且做这路生意的大概是“老鹰不吃窝下食”，本村倒不必担心 的；只要自己夜里警醒点就是了。秀才听了这“庭训”㈨，非常之以为然，便即刻撤消了驱逐阿Q的提议，而且叮嘱邹七嫂，请伊千万不要向人提起这一段话。abc但第二日，邹七嫂便将那蓝裙去染了皂，又将阿Q可疑之点传扬出去了，可是确没有提起秀才要驱逐他这一节。然而这已经于阿Q很不利。最先，地保寻上门 了，取了他的门幕去，阿Q说是赵太太要看的，而地保也不还并且要议定每月的孝敬钱。其次，是村人对于他的敬畏忽而变相了，虽然还不敢来放肆，却很有远避的 神情，而这神情和先前的防他来“嚓”的时候又不同，颇混着“敬而远之”的分子了。abc只有一班闲人们却还要寻根究底的去探阿Q的底细。阿Q也并不讳饰，傲然的说出他的经验来。从此他们才知道，他不过是一个小脚色*，不但不能上墙，并且不 能进洞，只站在洞外接东西。有一夜，他刚才接到一个包，正手再进去，不一会，只听得里面大嚷起来，他便赶紧跑，连夜爬出城，逃回未庄来了，从此不敢再去 做。然而这故事却于阿Q更不利，村人对于阿Q的“敬而远之”者，本因为怕结怨，谁料他不过是一个不敢再偷的偷儿呢？这实在是“斯亦不足畏也矣”㈩。abc第七章　革命abc宣统三年九月十四日（）——即阿Q将搭连卖给赵白眼的这一天——三更四点，有一只大乌篷船到了赵府上的河埠头。这船从黑魆魆中荡来，乡下人睡得熟，都没有知道；出去时将近黎明，却很有几个看见的了。据探头探脑的调查来的结果，知道那竟是举人老爷的船！abc那船便将大不安载给了未庄，不到正午，全村的人心就很动摇。船的使命，赵家本来是很秘密的，但茶坊酒肆里却都说，革命党要进城，举人老爷到我们乡下来 逃难了。惟有邹七嫂不以为然，说那不过是几口破衣箱，举人老爷想来寄存的，却已被赵太爷回复转去。其实举人老爷和赵秀才素不相能，在理本不能有“共患难” 的情谊，况且邹七嫂又和赵家是邻居，见闻较为切近，所以大概该是伊对的。abc然而谣言很旺盛，说举人老爷虽然似乎没有亲到，却有一封长信，和赵家排了“转折亲”。赵太爷肚里一轮，觉得于他总不会有坏处，便将箱子留下了，现就塞在太太的床底下。至于革命党，有的说是便在这一夜进了城，个个白盔白甲：穿着崇正皇帝的素（）。abc阿Q的耳朵里，本来早听到过革命党这一句话，今年又亲眼见过杀掉革命党。但他有一种不知从那里来的意见，以为革命党便是造反，造反便是与他为难，所以 一向是“深恶而痛绝之”的。殊不料这却使百里闻名的举人老爷有这样怕，于是他未免也有些“神往”了，况且未庄的一群鸟男女的慌张的神情，也使阿Q更快意。abc“革命也好罢，”阿Q想，“革这伙妈妈的命，太可恶！太可恨！……便是我，也要投降革命党了。”abc阿Q近来用度窘，大约略略有些不平；加以午间喝了两碗空肚酒，愈加醉得快，一面想一面走，便又飘飘然起来。不知怎么一来，忽而似乎革命党便是自己，未庄人却都是他的俘虏了。他得意之余，禁不住大声的嚷道：abc“造反了！造反了！”abc未庄人都用了惊惧的眼光对他看。这一种可怜的眼光，是阿Q从来没有见过的，一见之下，又使他舒服得如六月里喝了雪水。他更加高兴的走而且喊道：abc“好，……我要什么就是什么，我欢喜谁就是谁。abc得得，锵锵！abc悔不该，酒醉错斩了郑贤弟，abc悔不该，呀呀呀……abc得得，锵锵，得，锵令锵！abc我手执钢鞭将你打……”abc赵府上的两位男人和两个真本家，也正站在大门口论革命。阿Q没有见，昂了头直唱过去。abc“得得，……”abc“老Q，”赵太爷怯怯的迎着低声的叫。abc“锵锵，”阿Q料不到他的名字会和“老”字联结起来，以为是一句别的话，与己无干，只是唱。“得，锵，锵令锵，锵！”abc“老Q。”abc“悔不该……”abc“阿Q！”秀才只得直呼其名了。abc阿Q这才站住，歪着头问道，“什么？”abc“老Q，……现在……”赵太爷却又没有话，“现在……发财么？”abc“发财？自然。要什么就是什么……”abc“阿……Q哥，像我们这样穷朋友是不要紧的……”赵白眼惴惴的说，似乎想探革命党的口风。abc“穷朋友？你总比我有钱。”阿Q说着自去了。abc大家都怃然，没有话。赵太爷父子回家，晚上商量到点灯。赵白眼回家，便从腰间扯下搭连来，交给他女人藏在箱底里。abc阿Q飘飘然的飞了一通，回到土谷祠，酒已经醒透了。这晚上，管祠的老头子也意外的和气，请他喝茶；阿Q便向他要了两个饼，吃完之后，又要了一支点过的 四两烛和一个树烛台，点起来，独自躺在自己的小屋里。他说不出的新鲜而且高兴，烛火像元夜似的闪闪的跳，他的思想也迸跳起来了：abc“造反？有趣，……来了一阵白盔白甲的革命党，都拿着板刀，钢鞭，炸弹，洋炮，三尖两刃刀，钩镰枪，走过土谷祠，叫道，‘阿Q！同去同去！’于是一同去。……abc“这时未庄的一伙鸟男女才好笑哩，跪下叫道，‘阿Q，饶命！’谁听他！第一个该死的是小D和赵太爷，还有秀才，还有假洋鬼子，……留几条么？王胡本来还可留，但也不要了。……abc“东西，……直走进去打开箱子来：元宝，洋钱，洋纱衫，……秀才娘子的一张宁式床（）先搬到土谷祠，此外便摆了钱家的桌椅，——或者也就用赵家的罢。自己是不动手的了，叫小D来搬，要搬得快，搬得不快打嘴巴。……abc“赵司晨的妹子真丑。邹七嫂的女儿过几年再说。假洋鬼子的老婆会和没有辫子的男人睡觉，吓，不是好东西！秀才的老婆是眼胞上有疤的。……吴妈长久不见了，不知道在那里，——可惜脚太大。”abc阿Q没有想得十分停当，已经发了鼾声，四两烛还只点去了小半寸，红焰焰的光照着他张开的嘴。abc“荷荷！”阿Q忽而大叫起来，抬了头仓皇的四顾，待到看见四两烛，却又倒头睡去了。abc第二天他起得很迟，走出街上看时，样样都照旧。他也仍然肚饿，他想着，想不起什么来；但他忽而似乎有了主意了，慢慢的跨开步，有意无意的走到静修庵。abc庵和春天时节一样静，白的墙壁和漆黑的门。他想了一想，前去打门，一只狗在里面叫。他急急拾了几块断砖，再上去较为用力的打，打到黑门上生出许多麻点的时候，才听得有人来开门。abc阿Q连忙捏好砖头，摆开马步，准备和黑狗来开战。但庵门只开了一条缝，并无黑狗从中冲出，望进去只有一个老尼姑。abc“你又来什么事？”伊大吃一惊的说。abc“革命了……你知道？……”阿Q说得很含胡。abc“革命革命，革过一革的，……你们要革得我们怎么样呢？”老尼姑两眼通红的说。abc“什么？……”阿Q诧异了。abc“你不知道，他们已经来革过了！”abc“谁？……”阿Q更其诧异了。abc“那秀才和洋鬼子！”abc阿Q很出意外，不由的一错愕；老尼姑见他失了锐气，便飞速的关了门，阿Q再推时，牢不可开，再打时，没有回答了。abc那还是上午的事。赵秀才消息灵，一知道革命党已在夜间进城，便将辫子盘在顶上，一早去拜访那历来也不相能的钱洋鬼子。这是“咸与维新”（）的时候 了，所以他们便谈得很投机，立刻成了情投意合的同志，也相约去革命。他们想而又想，才想出静修庵里有一块“皇帝万岁万万岁”的龙牌，是应该赶紧革掉的，于 是又立刻同到庵里去革命。因为老尼姑来阻挡，说了三句话，他们便将伊当作满zheng府，在头上很给了不少的棍子和栗凿。尼姑待他们走后，定了神来检点，龙牌固然 已经碎在地上了，而且又不见了观音娘娘座前的一个宣德炉（）。abc这事阿Q后来才知道。他颇悔自己睡着，但也深怪他们不来招呼他。他又退一步想道：abc“难道他们还没有知道我已经投降了革命党么？”abc第八章　不准革命abc未庄的人心日见其安静了。据传来的消息，知道革命党虽然进了城，倒还没有什么大异样。知县大老爷还是原官，不过改称了什么，而且举人老爷也做了什么 ——这些名目，未庄人都说不明白——官，带兵的也还是先前的老把总（）。只有一件可怕的事是另有几个不好的革命党夹在里面捣乱，第二天便动手剪辫子，听 说那邻村的航船七斤便着了道儿，弄得不像人样子了。但这却还不算大恐怖，因为未庄人本来少上城，即使偶有想进城的，也就立刻变了计，碰不着这危险。阿Q本 也想进城去寻他的老朋友，一得这消息，也只得作罢了。abc但未庄也不能说是无改革。几天之后，将辫子盘在顶上的逐渐增加起来了，早经说过，最先自然是茂才公，其次便是赵司晨和赵白眼，后来是阿Q。倘在夏天， 大家将辫子盘在头顶上或者打一个结，本不算什么稀奇事，但现在是暮秋，所以这“秋行夏令”的情形，在盘辫家不能不说是万分的英断，而在未庄也不能说无关于 改革了。abc赵司晨脑后空荡荡的走来，看见的人大嚷说，abc“豁，革命党来了！”abc阿Q听到了很羡慕。他虽然早知道秀才盘辫的大新闻，但总没有想到自己可以照样做，现在看见赵司晨也如此，才有了学样的意思，定下实行的决心。他用一支竹筷将辫子盘在头顶上，迟疑多时，这才放胆的走去。abc他在街上走，人也看他，然而不说什么话，阿Q当初很不快，后来便很不平。他近来很容易闹脾气了；其实他的生活，倒也并不比造反之前反艰难，人见他也客 气，店铺也不说要现钱。而阿Q总觉得自己太失意：既然革了命，不应该只是这样的。况且有一回看见小D，愈使他气破肚皮了。abc小D也将辫子盘在头顶上了，而且也居然用一支竹筷。阿Q万料不到他也敢这样做，自己也决不准他这样做！小D是什么东西呢？他很想即刻揪住他，拗断他的 竹筷，放下他的辫子，并且批他几个嘴巴，聊且惩罚他忘了生辰八字，也敢来做革命党的罪。但他终于饶放了，单是怒目而视的吐一口唾沫道“呸！”abc这几日里，进城去的只有一个假洋鬼子。赵秀才本也想靠着寄存箱子的渊源，亲身去拜访举人老爷的，但因为有剪辫的危险，所以也中止了。他写了一封“黄伞 格”（）的信，托假洋鬼子带上城，而且托他给自己绍介绍介，去进自由党。假洋鬼子回来时，向秀才讨还了四块洋钱，秀才便有一块银桃子挂在大襟上了；未庄 人都惊服，说这是柿油党的顶子（），抵得一个翰林（）；赵太爷因此也骤然大阔，远过于他儿子初隽秀才的时候，所以目空一切，见了阿Q，也就很有些不放 在眼里了。abc阿Q正在不平，又时时刻刻感着冷落，一听得这银桃子的传说，他立即悟出自己之所以冷落的原因了：要革命，单说投降，是不行的；盘上辫子，也不行的；第 一着仍然要和革命党去结识。他生平所知道的革命党只有两个，城里的一个早已“嚓”的杀掉了，现在只剩了一个假洋鬼子。他除却赶紧去和假洋鬼子商量之外，再 没有别的道路了。abc钱府的大门正开着，阿Q便怯怯的躄进去。他一到里面，很吃了惊，只见假洋鬼子正站在院子的中央，一身乌黑的大约是洋衣，身上也挂着一块银桃子，手里是 阿Q曾经领教过的棍子，已经留到一尺多长的辫子都拆开了披在肩背上，蓬头散发的像一个刘海仙（）。对面挺直的站着赵白眼和三个闲人，正在必恭必敬的听说 话。abc阿Q轻轻的走近了，站在赵白眼的背后，心里想招呼，却不知道怎么说才好：叫他假洋鬼子固然是不行的了，洋人也不妥，革命党也不妥，或者就应该叫洋先生了罢。abc洋先生却没有见他，因为白着眼睛讲得正起劲：abc“我是性*急的，所以我们见面，我总是说：洪哥（）！我们动手罢！他却总说道N o！——这是洋话，你们不懂的。否则早已成功了。然而这正是他做事小心的地方。他再三再四的请我上湖北，我还没有肯。谁愿意在这小县城里做事情。……”abc“唔，……这个……”阿Q候他略停，终于用十二分的勇气开口了，但不知道因为什么，又并不叫他洋先生。abc听着说话的四个人都吃惊的回顾他。洋先生也才看见：abc“什么？”abc“我……”abc“出去！”abc“我要投……”abc“滚出去！”洋先生扬起哭丧棒来了。abc赵白眼和闲人们便都吆喝道：“先生叫你滚出去，你还不听么！”abc阿Q将手向头上一遮，不自觉的逃出门外；洋先生倒也没有追。他快跑了六十多步，这才慢慢的走，于是心里便涌起了忧愁：洋先生不准他革命，他再没有别的 路；从此决不能望有白盔白甲的人来叫他，他所有的抱负，志向，希望，前程，全被一笔勾销了。至于闲人们传扬开去，给小D王胡等辈笑话，倒是还在其次的事。abc他似乎从来没有经验过这样的无聊。他对于自己的盘辫子，仿佛也觉得无意味，要侮蔑；为报仇起见，很想立刻放下辫子来，但也没有竟放。他游到夜间，赊了两碗酒，喝下肚去，渐渐的高兴起来了，思想里才又出现白盔白甲的碎片。abc有一天，他照例的混到夜深，待酒店要关门，才踱回土谷祠去。abc拍，吧……！abc他忽而听得一种异样的声音，又不是爆竹。阿Q本来是爱看热闹，爱管闲事的，便在暗中直寻过去。似乎前面有些脚步声；他正听，猛然间一个人从对面逃来了。阿Q一看见，便赶紧翻身跟着逃。那人转弯，阿Q也转弯，那人站住了，阿Q也站住。他看后面并无什么，看那人便是小D。abc“什么？”阿Q不平起来了。abc“赵……赵家遭抢了！”小D气喘吁吁的说。abc阿Q的心怦怦的跳了。小D说了便走；阿Q却逃而又停的两三回。但他究竟是做过“这路生意”，格外胆大，于是躄出路角，仔细的听，似乎有些嚷嚷，又仔细 的看，似乎许多白盔白甲的人，络绎的将箱子抬出了，器具抬出了，秀才娘子的宁式床也抬出了，但是不分明，他还想上前，两只脚却没有动。abc这一夜没有月，未庄在黑暗里很寂静，寂静到像羲皇（）时候一般太平。阿Q站着看到自己发烦，也似乎还是先前一样，在那里来来往往的搬，箱子抬出了，器具抬出了，秀才娘子的宁式床也抬出了，……抬得他自己有些不信他的眼睛了。但他决计不再上前，却回到自己的祠里去了。abc土谷祠里更漆黑；他关好大门，摸进自己的屋子里。他躺了好一会，这才定了神，而且发出关于自己的思想来：白盔白甲的人明明到了，并不来打招呼，搬了许多好东西，又没有自己的份，——这全是假洋鬼子可恶，不准我造反，否则，这次何至于没有我的份呢？阿Q 越想越气，终于禁不住满心痛恨起来，毒毒的点一点头：“不准我造反，只准你造反？妈妈的假洋鬼子，——好，你造反！造反是杀头的罪名呵，我总要告一状，看你抓进县里去杀头，——满门抄斩，——嚓！嚓！”abc第九章大团圆abc赵家遭抢之后，未庄人大抵很快意而且恐慌，阿Q也很快意而且恐慌。但四天之后，阿Q在半夜里忽被抓进县城里去了。那时恰是暗夜，一队兵，一队团丁，一 队警察，五个侦探，悄悄地到了未庄，乘昏暗围住土谷祠，正对门架好机关枪；然而阿Q不冲出。许多时没有动静，把总焦急起来了，悬了二十千的赏，才有两个团 丁冒了险，逾垣进去，里应外合，一拥而入，将阿Q抓出来；直待擒出祠外面的机关枪左近，他才有些清醒了。abc到进城，已经是正午，阿Q见自己被搀进一所破衙门，转了五六个弯，便推在一间小屋里。他刚刚一跄踉，那用整株的木料做成的栅栏门便跟着他的脚跟阖上了，其余的三面都是墙壁，仔细看时，屋角上还有两个人。abc阿Q虽然有些忐忑，却并不很苦闷，因为他那土谷祠里的卧室，也并没有比这间屋子更高明。那两个也仿佛是乡下人，渐渐和他兜搭起来了，一个说是举人老爷要追他祖父欠下来的陈租，一个不知道为了什么事。他们问阿Q，阿Q爽利的答道，“因为我想造反。”abc他下半天便又被抓出栅栏门去了，到得大堂，上面坐着一个满头剃得精光的老头子。阿Q疑心他是和尚，但看见下面站着一排兵，两旁又站着十几个长衫人物， 也有满头剃得精光像这老头子的，也有将一尺来长的头发披在背后像那假洋鬼子的，都是一脸横肉，怒目而视的看他；他便知道这人一定有些来历，膝关节立刻自然 而然的宽松，便跪了下去了。abc“站着说！不要跪！”长衫人物都吆喝说。abc阿Q虽然似乎懂得，但总觉得站不住，身不由己的蹲了下去，而且终于趁势改为跪下了。abc“奴隶性*！……”长衫人物又鄙夷似的说，但也没有叫他起来。abc“你从实招来罢，免得吃苦。我早都知道了。招了可以放你。”那光头的老头子看定了阿Q的脸，沉静的清楚的说。abc“招罢！”长衫人物也大声说。abc“我本来要……来投……”阿Q胡里胡涂的想了一通，这才断断续续的说。abc“那么，为什么不来的呢？”老头子和气的问。abc“假洋鬼子不准我！”abc“胡说！此刻说，也迟了。现在你的同党在那里？”abc“什么？……”abc“那一晚打劫赵家的一伙人。”abc“他们没有来叫我。他们自己搬走了。”阿Q提起来便愤愤。abc“走到那里去了呢？说出来便放你了。”老头子更和气了。abc“我不知道，……他们没有来叫我……”abc然而老头子使了一个眼色*，阿Q便又被抓进栅栏门里了。他第二次抓出栅栏门，是第二天的上午。abc大堂的情形都照旧。上面仍然坐着光头的老头子，阿Q也仍然下了跪。abc老头子和气的问道，“你还有什么话说么？”abc阿Q一想，没有话，便回答说，“没有。”abc于是一个长衫人物拿了一张纸，并一支笔送到阿Q的面前，要将笔塞在他手里。阿Q这时很吃惊，几乎“魂飞魄散”了：因为他的手和笔相关，这回是初次。他正不知怎样拿；那人却又指着一处地方教他画花押。abc“我……我……不认得字。”阿Q一把抓住了笔，惶恐而且惭愧的说。abc“那么，便宜你，画一个圆圈！”abc阿Q要画圆圈了，那手捏着笔却只是抖。于是那人替他将纸铺在地上，阿Q伏下去，使尽了平生的力气画圆圈。他生怕被人笑话，立志要画得圆，但这可恶的笔不但很沉重，并且不听话，刚刚一抖一抖的几乎要合缝，却又向外一耸，画成瓜子模样了。abc阿Q正羞愧自己画得不圆，那人却不计较，早已掣了纸笔去，许多人又将他第二次抓进栅栏门。abc他第二次进了栅栏，倒也并不十分懊恼。他以为人生天地之间，大约本来有时要抓进抓出，有时要在纸上画圆圈的，惟有圈而不圆，却是他“行状”上的一个污点。但不多时也就释然了，他想：孙子才画得很圆的圆圈呢。于是他睡着了。abc然而这一夜，举人老爷反而不能睡：他和把总呕了气了。举人老爷主张第一要追赃，把总主张第一要示众。把总近来很不将举人老爷放在眼里了，拍案打凳的说 道，“惩一儆百！你看，我做革命党还不上二十天，抢案就是十几件，全不破案，我的面子在那里？破了案，你又来迂。不成！这是我管的！”举人老爷窘急了，然 而还坚持，说是倘若不追赃，他便立刻辞了帮办民政的职务。而把总却道，“请便罢！”于是举人老爷在这一夜竟没有睡，但幸第二天倒也没有辞。abc阿Q第三次抓出栅栏门的时候，便是举人老爷睡不着的那一夜的明天的上午了。他到了大堂，上面还坐着照例的光头老头子；阿Q也照例的下了跪。abc老头子很和气的问道，“你还有什么话么？”abc阿Q一想，没有话，便回答说，“没有。”abc许多长衫和短衫人物，忽然给他穿上一件洋布的白背心，上面有些黑字。阿Q很气苦：因为这很像是带孝，而带孝是晦气的。然而同时他的两手反缚了，同时又被一直抓出衙门外去了。abc阿Q被抬上了一辆没有蓬的车，几个短衣人物也和他同坐在一处。这车立刻走动了，前面是一班背着洋炮的兵们和团丁，两旁是许多张着嘴的看客，后面怎样， 阿Q没有见。但他突然觉到了：这岂不是去杀头么？他一急，两眼发黑，耳朵里〔口皇〕的一声，似乎发昏了。然而他又没有全发昏，有时虽然着急，有时却也泰 然；他意思之间，似乎觉得人生天地间，大约本来有时也未免要杀头的。abc他还认得路，于是有些诧异了：怎么不向着法场走呢？他不知道这是在游街，在示众。但即使知道也一样，他不过便以为人生天地间，大约本来有时也未免要游街要示众罢了。abc他省悟了，这是绕到法场去的路，这一定是“嚓”的去杀头。他惘惘的向左右看，全跟着马蚁似的人，而在无意中，却在路旁的人丛中发见了一个吴妈。很久 违，伊原来在城里做工了。阿Q忽然很羞愧自己没志气：竟没有唱几句戏。他的思想仿佛旋风似的在脑里一回旋：《小孤孀上坟》欠堂皇，《龙虎斗》里的“悔不 该……”也太乏，还是“手执钢鞭将你打”罢。他同时想手一扬，才记得这两手原来都捆着，于是“手执钢鞭”也不唱了。abc“过了二十年又是一个……”阿Q在百忙中，“无师自通”的说出半句从来不说的话。abc“好！！！”从人丛里，便发出豺狼的嗥叫一般的声音来。abc车子不住的前行，阿Q在喝采声中，轮转眼睛去看吴妈，似乎伊一向并没有见他，却只是出神的看着兵们背上的洋炮。abc阿Q于是再看那些喝采的人们。abc这刹那中，他的思想又仿佛旋风似的在脑里一回旋了。四年之前，他曾在山脚下遇见一只饿狼，永是不近不远的跟定他，要吃他的肉。他那时吓得几乎要死，幸 而手里有一柄斫柴刀，才得仗这壮了胆，支持到未庄；可是永远记得那狼眼睛，又凶又怯，闪闪的像两颗鬼火，似乎远远的来穿透了他的皮肉。而这回他又看见从来 没有见过的更可怕的眼睛了，又钝又锋利，不但已经咀嚼了他的话，并且还要咀嚼他皮肉以外的东西，永是不近不远的跟他走。abc这些眼睛们似乎连成一气，已经在那里咬他的灵魂。abc“救命，……”abc然而阿Q没有说。他早就两眼发黑，耳朵里嗡的一声，觉得全身仿佛微尘似的迸散了。abc至于当时的影响，最大的倒反在举人老爷，因为终于没有追赃，他全家都号啕了。其次是赵府，非特秀才因为上城去报官，被不好的革命党剪了辫子，而且又破费了二十千的赏钱，所以全家也号啕了。从这一天以来，他们便渐渐的都发生了遗老的气味。abc至于舆论，在未庄是无异议，自然都说阿Q坏，被枪毙便是他的坏的证据：不坏又何至于被枪毙呢？而城里的舆论却不佳，他们多半不满足，以为枪毙并无杀头这般好看；而且那是怎样的一个可笑的死囚呵，游了那么久的街，竟没有唱一句戏：他们白跟一趟了。"
            aqList = aq.split("abc")
        }
        emit("success")
    }


    /**
     * 获得第一页数据
     * @return Job
     */
    fun getRecords() = viewModelScope.launch {
        recordList.clear()
        recordData.value = recordList
        withContext(Dispatchers.IO) {
            val records = if (isLearnWord) {
                RedCatApp.appDatabase.recordDao().queryPageRecordWord(limit = pageLimit, 0)
            } else {
                RedCatApp.appDatabase.recordDao().queryPageRecord(limit = pageLimit, 0)
            }
            lastShowTimeStamp = records.reversed()[0].timestamp
            recordList.addAll(formartTimestamp(records.reversed()))
        }
        scrollToBottom.value = true
        recordData.value = recordList
    }

    /**
     * 初始化数据
     * @return Job
     */
    fun initData() = viewModelScope.launch {
        recordList.clear()
        recordData.value = recordList
        withContext(Dispatchers.IO) {
            val records = if (isLearnWord) {
                RedCatApp.appDatabase.recordDao().queryPageRecordWord(limit = pageLimit, 0)
            } else {
                RedCatApp.appDatabase.recordDao().queryPageRecord(limit = pageLimit, 0)
            }
            if (records.isNotEmpty()){
                lastShowTimeStamp = records.reversed()[0].timestamp
                recordList.addAll(formartTimestamp(records.reversed()))
            } else {
                lastShowTimeStamp = System.currentTimeMillis()
            }

        }
        LogUtils.e("recordList:" + recordList)
        scrollToBottom.value = true
        recordData.value = recordList
        initFinish.value = System.currentTimeMillis()
        isLearnWordData.value = isLearnWord
    }

    /**
     * 查询下一页
     * @return Job
     */
    fun getRecordNext() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            val records = if (isLearnWord) {
                RedCatApp.appDatabase.recordDao()
                    .queryPageRecordWord(limit = pageLimit, recordList.size)
            } else {
                RedCatApp.appDatabase.recordDao()
                    .queryPageRecord(limit = pageLimit, recordList.size)
            }
            recordList.addAll(0, nextformartTimestamp(records.reversed()))
        }
        recordData.value = recordList
    }

    /**
     * 下一页的时间戳 格式化
     * @param record List<Record>
     * @return Collection<RecordBean>
     */
    private fun nextformartTimestamp(record: List<Record>): Collection<RecordBean> {
        var data = ArrayList<RecordBean>()
        var tempTimeStamp = record[0].timestamp
        data.add(RecordBean(type = 7, showTimestamp = tempTimeStamp))
        record.forEach {
            if (it.timestamp - tempTimeStamp > 300000) {
                tempTimeStamp = it.timestamp
                data.add(RecordBean(type = 7, showTimestamp = tempTimeStamp))
            }
            data.add(
                RecordBean(
                    recordId = it.recordId,
                    type = it.type,
                    image = it.image,
                    text = it.text,
                    notice = it.notice,
                    wordName = it.wordName,
                    wordUs = it.wordUs,
                    wordTrans = it.wordTrans,
                    nickName = it.nickName,
                    avatar = it.avatar,
                    frame = it.frame,
                    veh = it.veh,
                    timestamp = it.timestamp
                )
            )
        }

        return data
    }

    /**
     * 第一页时间戳格式化
     * @param records List<Record>
     * @return List<RecordBean>
     */
    private fun formartTimestamp(records: List<Record>): List<RecordBean> {

        var data = ArrayList<RecordBean>()
        data.add(RecordBean(type = 7, showTimestamp = lastShowTimeStamp))
        records.forEach {
            if (it.timestamp - lastShowTimeStamp > 300000) {
                lastShowTimeStamp = it.timestamp
                data.add(RecordBean(type = 7, showTimestamp = lastShowTimeStamp))
            }
            data.add(
                RecordBean(
                    recordId = it.recordId,
                    type = it.type,
                    image = it.image,
                    text = it.text,
                    notice = it.notice,
                    wordName = it.wordName,
                    wordUs = it.wordUs,
                    wordTrans = it.wordTrans,
                    nickName = it.nickName,
                    avatar = it.avatar,
                    frame = it.frame,
                    veh = it.veh,
                    timestamp = it.timestamp
                )
            )
        }

        return data
    }

    /**
     * 新消息
     * @param record Record
     */
    fun addTimestamp(record:Record){
        if (record.timestamp - lastShowTimeStamp > 300000) {
            lastShowTimeStamp = record.timestamp
            recordList.add(RecordBean(type = AppConfig.type_timestamp, showTimestamp = lastShowTimeStamp))
        }
        recordList.add(
            RecordBean(
                recordId = record.recordId,
                type = record.type,
                image = record.image,
                text = record.text,
                notice = record.notice,
                wordName = record.wordName,
                wordUs = record.wordUs,
                wordTrans = record.wordTrans,
                nickName = record.nickName,
                avatar = record.avatar,
                frame = record.frame,
                veh = record.veh,
                timestamp = record.timestamp
            )
        )

    }

    /**
     * 插入记录
     * @param record Record
     * @return Job
     */
    fun insertRecord(
        type: Int,
        //图片
        image: Int? = null,
        //文字
        text: String? = null,
        //系统通知
        notice: String? = null,
        //系统单词
        wordName: String? = null, wordUs: String? = null, wordTrans: String? = null,
        //用户
        nickName: String? = null, avatar: Int? = null, frame: Int? = 0, veh: Int? = 0
    ) = viewModelScope.launch {
        withContext(Dispatchers.IO) {

            var record =  Record(
                type = type,
                image = image,
                text = text,
                notice = notice,

                wordName = wordName,
                wordUs = wordUs,
                wordTrans = wordTrans,

                nickName = nickName,
                avatar = avatar,
                frame = frame,
                veh = veh,
                timestamp = System.currentTimeMillis()
            )
            RedCatApp.appDatabase.recordDao().insertRecord(record)

            addTimestamp(record)
        }
        scrollToBottom.value = true
        recordData.value = recordList

    }


    /**
     * 首次使用
     */
    fun sayHello() = viewModelScope.launch {
        if (isFirst) {
            //产品介绍
            var hello = "欢迎${userData.nickName},进入自习室，我是自习室的管理员呀哈哈"
            var hello1 = "我们的自习室，主要目标是学习英语单词，每天任务是学习50个单词。"
            var ins = "在这里，你可以通过学习单词来赚取金币"
            var ins1 = "每学习一个单词会奖励10个金币，每天完成任务额外奖励100个"
            var ins2 = "金币有什么用？要不，你慢慢探索吧？"
            insertRecord(AppConfig.type_sys_text, text = hello)
            delay((80 * hello.length).toLong())
            insertRecord(AppConfig.type_sys_text, text = hello1)
            delay((80 * hello1.length).toLong())
            insertRecord(AppConfig.type_sys_text, text = ins)
            delay((80 * ins.length).toLong())
            insertRecord(AppConfig.type_sys_text, text = ins1)
            delay((80 * ins1.length).toLong())
            insertRecord(AppConfig.type_sys_text, text = ins2)
            delay((80 * ins2.length).toLong())
            isFirst = false
        }

        randomEvent()
    }

    /**
     * 随机事件
     */
    fun randomEvent() = viewModelScope.launch {
        while (true) {
            when ((1 until 10).random()) {
                1 -> {
                    creatAQ()
                }
                2 -> {
                    val user = UserBean(
                        nickName = creatNickName(),
                        avatar = creatAvatar(),
                        frame = creatFrame(),
                        veh = creatVeh()
                    )
                    insertRecord(AppConfig.type_sys_notice, text = "欢迎${user.nickName}进入自习室")
                    intoRoom.value = user
                }
                3 -> {
                    val image = creatImage()
                    insertRecord(
                        AppConfig.type_sys_pic,
                        image = image,
                        nickName = creatNickName(),
                        avatar = creatAvatar(),
                        frame = creatFrame(),
                        veh = creatVeh()
                    )
                    playSvga.value = image
                }
                4 -> {
                    val luxun = "行行行，你说得对"
                    insertRecord(
                        AppConfig.type_user_text,
                        text = luxun,
                        nickName = userData.nickName,
                        avatar = userData.avatar,
                        frame = userData.frame,
                        veh = userData.veh
                    )
                }
                5 -> {
                    val image = creatImage()
                    insertRecord(
                        AppConfig.type_user_pic,
                        image = image,
                        nickName = userData.nickName,
                        avatar = userData.avatar,
                        frame = userData.frame,
                        veh = userData.veh
                    )
                    playSvga.value = image
                }

                else -> {

                }
            }

            delay(40000)
        }


    }

    /**
     * 随机创建昵称
     * @return String
     */
    fun creatNickName(): String {
        var nickNameList = ArrayList<String>()
        nickNameList.add("轻狂、书生")
        nickNameList.add("落单恋人")
        nickNameList.add("低沉旳呢喃")
        nickNameList.add("宝宝猪")
        nickNameList.add("猪猪公主")
        nickNameList.add("猴哥")
        nickNameList.add("八戒")
        nickNameList.add("小白")
        var index = (1 until nickNameList.size).random()
        return nickNameList[index]
    }

    /**
     * 随机创建头像
     * @return Int
     */
    fun creatAvatar(): Int {
        return (1 until 8).random()
    }

    /**
     * 随机创建图片
     * @return Int
     */
    fun creatImage(): Int {
        return (1 until 8).random()
    }

    /**
     * 随机创建车架
     * @return Int
     */
    fun creatVeh(): Int {
        return (1 until 9).random()
    }

    /**
     * 随机创建头像框
     * @return Int
     */
    fun creatFrame(): Int {
        return (1 until 9).random()
    }

    /**
     * 创建单词
     */
    fun creatWord() {
        insertRecord(
            AppConfig.type_sys_word,
            wordName = wordsList[wordsIndex].name,
            wordUs = wordsList[wordsIndex].usphone,
            wordTrans = wordsList[wordsIndex].trans?.get(0).toString()
        )
        wordsIndex++
        userData = UserBean(coin = userData.coin + 10)
    }

    fun creatAQ() {
        insertRecord(
            AppConfig.type_sys_text, text = aqList[aqIndex], avatar = 7, nickName = "鲁迅"
        )
        aqIndex++
    }


    /**
     * 切换模式 isLearn   true 学习模式  false 闲聊模式
     */
    fun changeLearnModel() {
        isLearnWord = !isLearnWord
        isLearnWordData.value = isLearnWord
        getRecords()
    }
}