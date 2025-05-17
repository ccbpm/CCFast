var u=Object.defineProperty;var P=(i,n,o)=>n in i?u(i,n,{enumerable:!0,configurable:!0,writable:!0,value:o}):i[n]=o;var w=(i,n,o)=>P(i,typeof n!="symbol"?n+"":n,o);var x=(i,n,o)=>new Promise((m,p)=>{var d=e=>{try{r(o.next(e))}catch(s){p(s)}},I=e=>{try{r(o.throw(e))}catch(s){p(s)}},r=e=>e.done?m(e.value):Promise.resolve(e.value).then(d,I);r((o=o.apply(i,n)).next())});import E from"./Entity-Bn0jTX0h.js";import{b9 as B,aM as b,aB as f,aC as F,aH as D,bc as K,aL as N,H as M}from"./entry/index-M8VErHPE-1727507756861.js";import{MapData as T}from"./MapData-lfC2UY9r.js";import{downloadByData as g}from"./download-D69QRJaR.js";import"./Request-Cjt3q8nD.js";import"./form-BDuC2mcV.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";import"./EnumLab-CzismWql.js";import"./base64Conver-t-3tszFb.js";class v extends B{constructor(){super("GPN_FlowExpImp");w(this,"ImpExcelFlowDB",`
  #### 帮助
   - 解决其他异构的流程数据导入到本流程模板的过程.
   - 解决历史已经完成的流程在ccbpm进行查询分析.
  ##### 选择模式说明
   - 模板数据格式为excel2013版本以上.
   - 必须有: 实例主键、流程标题、流程发起人账号、流程发起日期必选字段字段.
   - 发起日期格式为: yyyy-MM-dd HH:mm
   - 导入的流程是已经完成的流程.
  `);w(this,"Imp",`
  #### 帮助
   - 上传模板、选择模式进行导入流程操作.
  ##### 选择模式说明
   - 作为新流程导入1：由ccbpm自动生成新的流程编号
   - 作为新流程导入2：使用流程模版里面的流程编号，如果该编号已经存在系统则会提示错误
   - 作为新流程导入3：使用流程模版里面的流程编号，如果该编号已经存在系统则会覆盖此流程
  `);w(this,"Exp",`
  #### 关于流程模板
   - ccbpm生成的流程模版是一个特定格式的xml文件。
   - 它是流程引擎模版与表单引擎模版的完整的组合体。
   - ccbpm的jflow与ccflow的流程引擎导出的流程模版通用。
   - 流程模版用于流程设计者的作品交换。
   - 在实施的过程中，我们可以把一个系统上的流程模版导入到另外一个系统中去。
    
  `);this.PageTitle="导入导出"}Init(){this.AddGroup("A","导入模板"),this.FileUpload("Imp","导入流程模板","请上传符合ccform表单格式的模式",this.Imp);const m=b.AtParaStringToJson("@0=作为新流程导入@1=作为新流程导入2@2=作为新流程导入3"),p=Object.keys(m),d=[];for(const I of p)d.push({No:I,Name:m[I]});this.SelectItemsByList("Imp.Way","选择模式",this.Imp,!1,JSON.stringify(d)),this.AddGroup("B","导出"),this.AddBlank("Exp","导出流程模板",this.Exp),this.AddBlank("DTSField","检查模板字段",this.HelpUn),this.AddGroup("C","流程数据导入"),this.FileUpload("ImpExcelFlowDB","Excel模式导入","请上传符合ccform表单格式的模式",this.ImpExcelFlowDB)}GenerSorts(){return x(this,null,function*(){return Promise.resolve([])})}Save_TextBox_X(o,m,p,d,I){return x(this,null,function*(){const r=this.PKVal;if(o=="Imp.Way"){const e=new M("BP.WF.HttpHandler.WF_Admin_AttrFlow");e.AddFile(this.UploadFile),e.AddPara("FlowNo",r),e.AddPara("ImpWay",p);const s=yield e.DoMethodReturnJson("Imp_Done");return new f(F.Message,(s==null?void 0:s.Msg)||"创建成功")}if(o=="Exp"){const e=new E("BP.WF.Flow",r);yield e.Init();const s=new M("BP.WF.HttpHandler.WF_Admin_CCBPMDesigner");s.AddPara("FlowNo",r);const l=yield s.DoMethodReturnString("ExpFlowTemplete");return g(l,e.getData().Name+".xml","xml"),new f(F.DoNothing,"")}if(o=="ImpExcelFlowDB"){const e=new M("BP.WF.HttpHandler.WF_Admin_AttrFlow");e.AddFile(this.UploadFile),e.AddPara("FlowNo",r);const s=yield e.DoMethodReturnJson("Imp_ImpExcelFlowDB");return new f(F.Message,(s==null?void 0:s.Msg)||"导入信息如下:"+s)}if(o=="DTSField"){const e=["WF_Flow","WF_Node","WF_Cond"],s=JSON.parse(yield D.toJSON([])),l=new T;for(const c of e){l.setPKVal(c),(yield l.IsExits())==!1&&(l.Name=c,l.PTable=c,yield l.Insert());const y=new K;yield y.Retrieve("FK_MapData",c);const _=s.filter(h=>h.PTable==c);for(const h of _){const A=(yield D.GetEn(h.No))._enMap.attrs;for(const a of A){if(y.find(U=>U.KeyOfEn===a.Key))continue;const t=new N;t.FK_MapData=c,t.KeyOfEn=a.Key,t.Name=a.Desc,t.MyDataType=a.MyDataType,t.UIContralType=a.UIContralType,a.IsEnum&&(t.LGType=1),a.IsFK&&(t.LGType=2),t.UIWidth=a.UIWidth,t.UIHeight=a.UIHeight,t.MinLen=a.MinLength,t.MaxLen=a.MaxLength,t.UIBindKey=a.UIBindKey,t.UIRefKey=a.UIRefKeyValue,t.UIRefKeyText=a.UIRefKeyText,t.UIVisible=a.UIVisible,t.UIIsEnable=a.UIIsReadonly,t.UIIsLine=a.UIIsLine,t.DefVal=a.DefaultVal,t.MyPK=t.FK_MapData+"_"+t.KeyOfEn,yield t.Insert(),y.push(t)}}}}return new f(F.Message,"检查成功.")})}}export{v as GPN_FlowExpImp};
