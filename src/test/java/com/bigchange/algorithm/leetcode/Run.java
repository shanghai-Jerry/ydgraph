package com.bigchange.algorithm.leetcode;

import java.util.ArrayList;
import java.util.List;

/**
 * User: JerryYou
 *
 * Date: 2019-05-08
 *
 * Copyright (c) 2018 devops
 *
 * <<licensetext>>
 */
public class Run {

  public void println(Object o) {
    System.out.println(o);
  }

  public static void main(String[] args) {
    Run run = new Run();


    RemoveAllAdjacentDuplicatesInString1047 removeAllAdjacentDuplicatesInString1047 = new RemoveAllAdjacentDuplicatesInString1047();

    run.println("out:" + removeAllAdjacentDuplicatesInString1047.removeDuplicates("cjpgjsomosejmrphpsirmhrhestdcigpbpfkhrgptjelkjrqqogcoabkntfdodoegtgbeidsonksnaegofrqndhlepfnjnjbfoqmhbecjsmnjqijplarpjetttcdmaeeottljcgskjigtcocalctttdqtslpjdglqqnndqicjsehblbqicqdjlfqpbfgoslrtjjakacttignfjklqpofjdjoeqficrmljhdkjrbqeimecceamgemljapkcbejeqhqgkeqoajenmdeehstjlbqpqcjtgallepbnajtbegcitpprebcbaqoimpiiigqkhppeltkeempqfamtcnbjgrhpkgtbpdfrnrtaambbeohocsfaohtleqoosemrdliallfnifjdnqjbjbekdcnhgediairamjdhaaegrgejchsoaaetootohslmfoodnkjonefiigqjicnjkqjbniqdjcqmacrcagcfbiqsjlagcjtjordchcflkaelkniehboibdamtpmeiqqospkbnpgktirjgopoetsmqjoiqtflheermfpgjmchmfjgbkapnhqhotjqplrhgmkcjraihdksrelgshfetcostililosmcqternhjkcoaortjbpcnselcnegfcfhfsncleghmkmjblacdtrnqkrpbrpgsoemqhbeiktajigqqqmselcqetqomjoqmmdnapgrtdcgqlccofsnmgatdjdaaaamgeckgrmihpjsbidrcfooptfprqeklpfkrkqpepnpjsjkabrkjkqnnojethaohbnnrgsrhfplmtjqnjnikoeggqogrikkolrsbaethdktrqhlaogfcprfomkqcjqngemekesmqsifipbtneqsapipfpmpslepbqsjlkedknprpraiahatrhrodejtlldjneekarnjjngnfkidnamdrhqinhdpshjrqpncsdolcnfhatngipkskteenniebqjnecntrgtchsrdmgsdbocotttsifebmorpqmfcmqemjlnhcpbbiacjctpctmftkntjmrpkmpitranbcmaqssqapgabinrcaeigpcjiqeqkncbfhalibgdseginqnlccjhstshijdafsagnfhaboqmigfneeddkodocdnhgdiiiftpdrijpoojslffrspkgdfhqlqdnbtqimkqddjpphdmphejqaotmhndodpkjptlpnfctsmkekabrfpifelgckjsplfbaggkhgrebtkksqdsnskbqismrfghpssoqfprlrbkfrtsrdqfoffpkcqeiamqckdlbjnitrsjqrtqjbnrsladreqcaopdqkpijocotrhlgosokqnljtljlrdsbttrkkgtshqeihqstfmqrsrjrcknteqnsqijkprsinaoescbkrnabrdrmlbgblarijkfcptqftsqrfafetnerpttshdngbcjlratfdfnhroogolfqhpopdibnggsqssgcfeaphcksfqtdikniqfipmcbhdqfnkbokeiqepssemmaaprjbkrktqosessnpisitdcpbpsflbsjjpjggcboacpanaeqmkbaibimqicahbbkpqnljelclsfmkbgmdlhpotlahqmogtpoargqhdqtpdnrpmicatqanqtlrmkprjklkehcllafidklpnsbqchpajejcjeppdhlaibassdmcjbtdkipraemmbofgbnpskhgkjdoqffhjbjlhescbrlafnjrbfbspnmkelnijgohkptognttpjfosjnlqcficjdjpdmtqrfillcjbfjsliggaqnigkajsginnthcrnmsgfgkdtcsbsbjdktpohtgmddimmgnslnmsnqtsqqjjdgssejostaklamslggqntgmldhphafbeafjccrpfaojaccncrsqdhnijlihrfsdellgstntijpqdjhdrbhqtslbifrrcspsprikpicknrpsaabnnlmrkkigbbneljmqqchabaipocctciodercbmpmjdhcipsfddhqmmehdgtrigiqfqqecmklefiighooeegalttlpitatdnqkhfctdhrafktjtbohmekalsqmcqrskschhcfjrfsrgkmjtcdmrcollmrjqntnhmkamimgjancrapccfpcjoriaoqnqolgjomniaaeinirablfammopsomsldleophbfakmqqpbqnandantiimhmokqimqgpdhlnckbkenoigedmsontmanoikinfmmmghdfsscqfmjgbenkcrljifrgbipbdobrabenhotacpcrpcgcefenarqcenaiomnkflskpgiratckmepndgrmqkarberpsaflcrlmijrgksbmilrntmcnmbtsrgpcpppapbrotaofmchehpnqreoberedlrfejajbhjmqetabpphfbdjrarkmdepdllqrarrskqkapsfrkdsebrhlqtgftijjclihiqjljsbpmfltgqpbbdkeshrackmlaoitmoibdinlhtnokbqteoskbpaocjkihhfqsdpsibmeajdgsdoorkaloghgrtbanclhitalfjmrhlkttaatjokginrfqsphopfldnnqfjbtejaiakikqhodiklttttfjlqntgqsfimnsstpojlopjlcadehkgnkkjameotdmobilmctmhetembottcsjtgbgbmkcnbqtlqenfjipcmtimqfchlpqjqmqtbdbskgfqmibkqocjdlciesarpttgntnioibhtpcismtgpnbengmnbboelrriqqqhnqockjphkntlgrrmjnndqkgidgihjmjqimmlbgpbtlnlohjmhlesmflklfcrqhbepmceticlcfrhsjrqnbpgqosjkdfqbfpcddnmqjjakllgjmgjjcakpfgfrhpffcagakkoitqiaglsfatjiirlsgaddcjekolioehllqjmagqfgbjojhinkbhjrlpfgoqnmfjofstlkkqesilmrekqqoprhdjmeeaqddardadrjtrtopobmctlkorpjcdaqjljopekajldjeqkttnpfkhtghabkpjkosdbkmrkalcdecoqmbirrmcmtsbntoospfogplhntjbtiqjcjpamqppcfdpcdqqdrhehbljprkhdslsmsomhndarepsndbjlsnantsnhkabrjgrgdhjobohmjmlmdidarjsgoisdpgohgscmnqgfeboqkhfbckqhsjtjrkdfkmoefdmnottjfgljnfrejgnhrlcgfhrfroonjqbkegjmnnihbqoteagogbdfkmebhmsehratsdtfpjtfsgiegktkcflttrnlicidphaqejorntreajegmniaprbtbnsrnagsicsmghboqkkeidjkoqfatisbmeeiasalqbhqrdeltlkrqciktfcsbbfkmtgttebmmefjheeiisqtgaeimaonjclrmssfrkfekqbicrmserkbfpbmncdkqstkiedfoqmhgjtafintdldarneancdjaffkssmeckqsmolpiicsikgagbfkbfmotccdqdlorbcehejgdjpaqgrjmkgfnlabptjpcqkotlebmoqnghrtgeokiknjmhqbspdgcqgcbrjsdjsakfhcpmkbrmctlnidhstgcimaopgktefhbjcntbjmkqescsedoirfcdofbconjmdfbsnmjihmbslomkefmbcktgbfmqjhrbctrafilaamrbiioeiitjbsnnjplomngjafjpfmgbeclhktprafbqsmedfqibpsjdpgrpnfbnhqrtjlnelingkilmdbetqonnamomgjkbiprjkacohjdpfclnglaocfattnsltosqbdnrftgkomhdgsisfjlqkmtsqchsalqpnlhhaksbrqjobrrsgrftconsshiccbfnpesdsaokrsgnlsprhjcfrhqcdtrlmpjaahcfqggoqoiclormbtkmqirdgtkbdflaijericrfhirgjabtedglcelmcdprgtiolroefharklifcqqrmdijilegfdrnskenkfkaabcrfmnmfgjtrfoirgnmjdtogjcckthoenqpptifhkselotdrtrplcmjsetjgbbcccohseoftfebkgdbjqoctgcqllhbfpqnfqdsjprffssjoncqirrknjrqmdrsttmrnheaptlhgsjbosjhdijgfphsshkgppcdlijeonlbnpfaqlgknddhlkbeihkcheeeokcdmjrrtdclkohjcrliliaakqiebkmgpjomspbnpjlcrldqppqgiopennbicnforcnamttdkonmgqotdtpfairoqoibgnlbmmnooimrhfhbdhektlqmbgnnedhqnfkfrtepehcjkcgsnbrjslgtjdimifaqdgtqalcgcqlbafodesneafcpcftbqptgieaqhbrqakkmmlopmsrpctmanghdjrroaktpqehmbllostddmrecmapnooobgmkptslqhqeptntsdnrocosjmmqhjlsnelcrqseohocrmnmstrpemstqaapfspsogkmrbdohiqhnspebmqcpmjdhappifesiqbngeqbchrhhpgjlnrceissglfbdkdtklnqntdcgtgmmearadrksklrsftnheplkljgcicljijpmfptlmohdekdflasdfdihcndqqkgsdndkkapodojtpeqeembmdgrjogpadpkbpogopokifqllhniiqlcbajrqlornbbpgnebgmdcjbbskemppasqdkqteelrpchfrqmmpbjffepkrconkhaesgkqfjfressnbfbcpfnoifekmilpkcfnnifnploljmkjmlhqkctntmatoedpriramidlcjemfgppbcprtksgegfalecdncrfegfflthlfsacepqthcinqonhaspcmtqifklcakjfmghkfjcpclhttnhffileatcqklemscegeiopfekaodegnkmbrdcrqtqkatcdjjpjnrkcsnkqghoegssfbhacdcfrcbtnnfohebkrreqmmhpqdlpkinnnpbrcaqlhhckmiqeggiqlqtrcoitpgdmmppfbefacnhchsgomnficlsnsekqcrjkskapchejbeqsjcprkklcnfgtnodqihrltcrjaeojarocgphbtglnergonshgmqkptkjphjnbhsahrobrmhfgkdjimgnkqbdjlsfhjskesrdiholpgsddpbidnqjjfnscrcalimqikqoketljoqekfqimqomscnbchrkkncnhbbblholahtfhckdpbofjmaobibibtfqsfsrnjtjpnbeedpstqoedlldtajjkpjijbjnlcfciaanhlkrnsagdtkfttjsdjnhenessbbonqjtthmignckbhmmcfjsimnphjndilordjjgbtalthdjfpreahfbdrcnbgpmthnlsqdclsdghdkciflnbpnikeakhsnkdkcqsaofdfnmqbnobtloqlrgmacqrlnmkmapoeimbmrdintembpbdgcincfmkmgdkjtsdstntspbfilejndfmignggagmfisseqtjsnplmmadedtnrcniboogtijngtrogseeonpaktkidgetrfkcokbnoahgdkrtggplthigdjeoimipdtrijknihjdcrpsijejqcgsgikkshfggfjaaettkrkehkbcflhhjcectmjmeersdlcahkpclbealemldeehktaqjndceeerkptrqldplkcrhcmbrhksfbkojblinjcothmlrtqetcfilibddfklaqskcagjjjtotdmcicaqclnrpkdjjnpejiqeepltkhtmfmlrkrfierfrsfcimabigkihtcsrqhgansogdpfpjjrondsdtogkhfohqrbobnqternmhtgoopkaasigssiqkrgbjhghpjjijmlsnhgikpmrlnoonnpmfsaajrrrlmfqeiefcglffioarmrpbdamriajjodkbebobjrskblkqleqetfhfhkkslcdtlptfthcbcodboeokdihkggicggpaegckaoqsqejtmsfltnntjppegfosaltkjqtcbabklbshggalsbaartojseipaccbiafabifdmicojjmofkhdgaotfmcmdpanatbdbkocekiljmealtnskgembgrobracbdctmhhgrmqocgsrjiofotmjfaidfneeariehdpfrqhhdanenmcgjgocgaajmcoolcahorlrtmfpikacenodnbcksqeqcainainlajqhhsmrldmphoolhqqohgakkfefbeaqkbaobaorclgdfprlcmdlimnrbktomhapekqqfidmclqccmdbtentemgepbkpihnpflitaegikshdorjdgfeksaqitklskoqesnqaakhtdnptrkcbshgchgcesnljicagdledtqhfjaqkmlsrafffqrqadqkeinssptlqijjqtscmsmgnqakmkileaglbmjjpdbdcdmnccarmsfbqjdjphroqgneosefbpgdkipqosraqssmcaggrjtcjebqqbbtjsfkqibkrkkrojdgqjtnedscgaholjdknbhnqngfodossabdnpdosrocqldittilqeijgjkeffkjgbkmjdrjmikhraqaerdjhcterdmekpnqajlnbrorfsqjpfkhrfajiqalopkdsgeioreeieridfpckpkjpbocggakfnlrmgcjkacpgjmmqtleqnmcsdhebtpnnkhemgateclkhppsjoelprtgnpgeraaaqassodrokhmmmddlkggcsqabgptfliekirqtdfogkqcjindndrbbbrchpashkncncqnkcdddnttspjijpibnnrotfldtobptcbbdlgpedcjknsoldicnbqtdatsbiqocienlpcghaqsrdreldpnfnrgclhthpleatndsjsnktjaeifkndtiogfstefsdhgagqjicjpikjfbtqqoaktmqgemdfftbfnhrpkaftacigqtjmlfokqffhfhsprkqtdiheslhmoeggofstrfnbqdhjnqabodatfifgmkkdrrrbmkcbegteihkcqtrnncorchgcqlfijobonecpbhccrljejprbjebtpqmaotjqfkkofssdpftjgqqljaqsolgpidsqllqrgctjilalddsllpenmdgiaiieqflsffsklfcdsfdtjshrekqebapktldhqofbopcebigphpabjcslfhbhfokkqbesirsojpajecogmfnhcrlelqccerfcjeoeeharsgfipqiqplgaigkoqdpsbdgdencgtlborreneconcjfjrldlccnfrfoiarhjbgsekdriitjjalbqhanqqddsacnpfkfcqppngtfckhflnrpdlccakekkbthghkrmkpehtlagqtsegtfkifinetbpblqsddsqdfbltrimthqabbdldmijppemdtehplnafoninpdrccthqlqeepcjbpqfnkhenrrogbkirqnmoahioraeomhjebepmobdqepccdlbhejnlktmctatkmrilebehljgiofcpedmeshbeafddbpcineqkmspicrobrcehphjoiegkfhjgtreddohrierelhdpthtgiallfpdnodgmgpjqfssagbqkrjetdcpcqidsjrnashjkeikgqenerqpaingljhrbligdmfnecidksbstahphfoghnmjfirepamroshejooecibrjpnnlknboqgrqgqmntqpmhanoafijhbdaaattifbpaloecfchjqdldtjihndlrenjrfnppjfhshlgiprrgdabkrpkfoericsekmllpfobjfjqatronhlninlcsqeboesbejaimokokhimgdgdtcalfpbctcpbeffdpanespdkiaaikondtoctnagcignmprqtobcjriaampnoikaeofcbipipmkbkgnsqnmhcogkfnkcbepcdeibccbcnheqpmftebfmphfajidingbetdhbqifjehccpptaqclqirdsmitptgnmsctcjqdtpbfdprbihpaqbmdibkolrqhjnhbgqtmdtdjkjipdlbcedrchakqtrjfppcatjflrtbknlpag"));
  }
}
