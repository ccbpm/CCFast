var W=Object.defineProperty;var P=(c,d,t)=>d in c?W(c,d,{enumerable:!0,configurable:!0,writable:!0,value:t}):c[d]=t;var F=(c,d,t)=>P(c,typeof d!="symbol"?d+"":d,t);var E=(c,d,t)=>new Promise((a,o)=>{var I=e=>{try{s(t.next(e))}catch(r){o(r)}},A=e=>{try{s(t.throw(e))}catch(r){o(r)}},s=e=>e.done?a(e.value):Promise.resolve(e.value).then(I,A);s((t=t.apply(c,d)).next())});import N from"./Entity-CqiUg-St.js";import{b5 as B,aM as y,G as n,l as m,H as h}from"./entry/index-C6uBgOW5-1730430676707.js";import{GroupField as M}from"./GroupField-DV5A1BJs.js";import{SFDBSrc as T}from"./SFDBSrc-DKIMsnoa.js";import u from"./Events-D9tOL1Ad.js";import{downloadByData as b}from"./download-DS4Mvmi4.js";import L from"./BSEntities-DAhkMRkM.js";import{Node as G}from"./Node-DKGUVcK1.js";import"./Request-CTraI-GH.js";import"./form-Bg9E1Us6.js";import"./vue-BXIlYw1E.js";import"./antd-Dd9L3uAF.js";import"./MapAttr-B1mxD3vP.js";import"./EntityOID-DvdPWQGp.js";import"./MapData-Ccsy8tbB.js";import"./EnumLab-CzismWql.js";import"./base64Conver-t-3tszFb.js";import"./EntityNodeID-BCBbJNH2.js";class re extends B{constructor(){super("GPN_FrmExpImp");F(this,"Imp",`
  #### 帮助
   - 选择的模版文件必须是驰骋表单引擎导出的格式为 .xml 的文件.
   - **导入后会清空当前设计的元素.**
  `);F(this,"ImpNodeFrm",`
  #### 帮助
   - 选择下列节点所绑定的表单进行导入.
   
  `);F(this,"ImpIsReadOnly",`
  #### 帮助
   - 选择从表单库导入的表单是否可编辑.
   
  `);F(this,"TableSrc",`
  #### 帮助
  - 从数据表结构导入字段然后生成表单.
  - 选择表结构.
  `);F(this,"TableSrc_Tables",`
  #### 帮助
  - 选择表.
  `);F(this,"TableSrc_Tables_Fields",`
  #### 帮助
  - 选择字段.
  `);F(this,"ImpEnsFrm",`
  #### 帮助
  - 根据实体类的属性进行导入.
  `);F(this,"Exp",`
  #### 帮助
   - 表单导出.
   - 请点击下一步进行下载.  
  `);F(this,"WordImpHelpUn",`
   #### 帮助
   - 视频教程：https://drive.weixin.qq.com/s?k=AOsAZQczAAY4qqF3E1
   - 此功能使用到了OFFICE API,后台只能发布在安装office或wps的windows系统中
   - 此功能只支持docx操作
   - 通过识别定义的标志对word进行解析操作
       1.自动生成表单
       2.自动生成rtf模版，在运行=》列表 ，双击已生成的数据时 可以在左侧看到“rtf生成文档”打印按钮
   #### 标志介绍
   - .XY （表格标志）
       第一行第一列单元格内容以.XY结尾 (行标题和列标题共同组成主表字段，数据以表单的形式逐个录入)
      ![输入图片说明](/resource/WF/Admin/FrmLogic/ImpExp/xy1.png "屏幕截图.png") 
      ![输入图片说明](/resource/WF/Admin/FrmLogic/ImpExp/xy2.png "屏幕截图.png") 
      ![输入图片说明](/resource/WF/Admin/FrmLogic/ImpExp/xy3.png "屏幕截图.png") 
   - .X  （表格标志）
      第一行第一列单元格内容以.X结尾(行标题组成主表字段，数据以表单的形式逐个录入)
     ![输入图片说明](/resource/WF/Admin/FrmLogic/ImpExp/x1.png "屏幕截图.png") 
   - .T  （表格标志）
      第一行第一列单元格内容以.T结尾 (行标题组成主表字段，数据以表单的形式逐个录入) 
     ![输入图片说明](/resource/WF/Admin/FrmLogic/ImpExp/t1.png "屏幕截图.png") 
   - .DTL （表格标志）  
      第一行第一列单元格内容以.DTL结尾(行标题组成主表字段，数据以表格形式插入多行数据)
      ![输入图片说明](/resource/WF/Admin/FrmLogic/ImpExp/dtl.png "屏幕截图.png") 
   - .* (表格标志)
      以此开头的单元格内容会保留原数据不会处理  
   - #PIC# （图片标志）
      在word文档需要插入图片的地方录入此标志(支持多图片上传)
      ![输入图片说明](/resource/WF/Admin/FrmLogic/ImpExp/pic.png "屏幕截图.png") 
  `);this.PageTitle="模板导入导出"}Init(){return E(this,null,function*(){this.AddGroup("A","模板导入");const t=this.RequestVal("FlowNo");if(t){const r=`SELECT NodeID as No, Name FROM WF_Node WHERE FK_Flow='${t}'`;this.SelectItemsByList("ImpNodeFrm","从节点上导入",this.ImpNodeFrm,!1,r)}this.SelectItemsByGroupList("ImpFlowFrom","从其它流程导入",this.ImpNodeFrm,!1,"SELECT No,Name FROM WF_FlowSort ","SELECT No,Name,FK_FlowSort FROM WF_Flow ");const I=[{No:"0",Name:"只读"},{No:"1",Name:"可编辑"}];this.SelectItemsByGroupList("ImpFrmID","从表单库导入",this.ImpNodeFrm,!1,y.srcFrmTree,y.srcFrmList),this.SelectItemsByList("ImpFrmID.IsReadOnly","导入是否只读",this.ImpIsReadOnly,!1,JSON.stringify(I)),this.FileUpload("Imp","导入表单模板","请上传文件",this.Imp),this.TextBox1_Name("ImpEnsFrm","从实体类导入",this.ImpEnsFrm,"className","","如：BP.Port.Emps"),this.SelectItemsByList("TableSrc","导入表结构",this.TableSrc,!1,"SELECT No, Name FROM Sys_SFDBSrc WHERE 1=1 "),this.SelectItemsByList("TableSrc.Tables","选择表",this.TableSrc_Tables,!1,this.GenerTables),this.SelectItemsByList("TableSrc.Tables.Fields","选择字段",this.TableSrc_Tables,!0,this.GenerTableFields),this.AddGroup("B","Office导入"),this.FileUpload("Excel","Excel表单模板","请上传Excel格式的表单模板","对excel内容作为表单的字段生成"),this.SelectItemsByList("Excel.FieldModel","选择模式",this.ImpNodeFrm,!1,"SELECT 0 AS No, '把列作为字段' AS Name FROM WF_Emp WHERE No='admin' UNION SELECT 1 AS No, '把内容作为字段' as Name FROM WF_Emp WHERE No='admin' "),this.SelectItemsByList("Excel.FieldModel.SelectField","选择字段",this.ImpNodeFrm,!0,this.GenerExcelFields),this.FileUpload("Word","Word表单模板","请上传Word格式的表单模板",this.WordImpHelpUn),this.SelectItemsByList("Word.FrmModel","表单模式",this.ImpNodeFrm,!1,"SELECT 0 AS No, '经典表单' AS Name FROM WF_Emp WHERE No='admin' "),this.AddGroup("C","表单模板导出","icon-layers"),this.AddBlank("ExpXml","导出xml模板",this.Exp),this.AddBlank("ExpExcel","导出Excel模板",this.Exp),this.AddGroup("D","表单数据导出","icon-directions"),this.AddBlank("ExpDataXml","导出xml格式",this.Exp),this.AddBlank("ExpDataExcel","导出Excel格式数据",this.Exp)})}GenerExcelFields(){return E(this,null,function*(){const t=this.RequestVal("FrmID")||this.RequestVal("PKVal"),a=new h("BP.WF.HttpHandler.WF_Admin_FoolFormDesigner_ImpExp");a.AddFile(this.UploadFile),a.AddPara("FrmID",t),a.AddPara("Model",this.RequestVal("tb1","Excel.FieldModel"));const o=yield a.DoMethodReturnString("Imp_ExcelFileds");return JSON.stringify(o)})}GenerTables(){return E(this,null,function*(){const t=this.RequestVal("tb1","TableSrc"),a=new T(t);yield a.RetrieveFromDBSources();const o=yield a.GenerTables();return JSON.stringify(o)})}GenerTableFields(){return E(this,null,function*(){const t=this.RequestVal("tb1","TableSrc"),a=this.RequestVal("tb1","TableSrc.Tables"),o=new T(t);yield o.RetrieveFromDBSources();const I=yield o.GenerTableFields(a);return JSON.stringify(I)})}GenerSorts(){return E(this,null,function*(){return Promise.resolve([])})}Save_TextBox_X(t,a,o,I,A){return E(this,null,function*(){const s=this.RequestVal("FrmID")||this.RequestVal("PKVal");if(t=="Word.FrmModel"){const e=new h("BP.WF.HttpHandler.WF_Admin_FoolFormDesigner_ImpExp");e.AddFile(this.UploadFile),e.AddPara("FrmID",s),e.AddPara("FrmModel",o);const r=yield e.DoMethodReturnString("Imp_WordFileSaveIt");return u.emit("reloadForm"),r.includes("err@")?new n(m.Error,r):new n(m.Message,r)}if(t=="Excel.FieldModel.SelectField"){const e=new h("BP.WF.HttpHandler.WF_Admin_FoolFormDesigner_ImpExp");e.AddFile(this.UploadFile),e.AddPara("FrmID",s),e.AddPara("Fields",o);const r=yield e.DoMethodReturnString("Imp_ExcelFileSaveIt");return u.emit("reloadForm"),r.includes("err@")?new n(m.Error,r):new n(m.Message,r)}if(t==="Imp"){const e=new h("BP.WF.HttpHandler.WF_Admin_FoolFormDesigner_ImpExp");e.AddFile(this.UploadFile),e.AddPara("FrmID",s);const r=yield e.DoMethodReturnString("Imp_LoadFrmTempleteFromLocalFile");return u.emit("reloadForm"),r.includes("err@")?new n(m.Error,r):new n(m.Message,r)}if(t==="ImpNodeFrm"||t==="ImpFrmID.IsReadOnly"||t==="ImpFlowFrom"){let e,r;if(t==="ImpFrmID.IsReadOnly"&&(e=this.RequestVal("tb1","ImpFrmID"),r=this.RequestVal("tb1","ImpFrmID.IsReadOnly")),t==="ImpNodeFrm"&&(e="ND"+o),t==="ImpFlowFrom"){const D=parseInt(parseInt(o)+"01"),x=new G(D);yield x.Retrieve(),e=x.NodeFrmID?x.NodeFrmID:"ND"+D}const i=new h("BP.WF.HttpHandler.WF_Admin_FoolFormDesigner_ImpExp");i.AddPara("FK_MapData",s),i.AddPara("FromFrmID",e),i.AddPara("IsClear",0),s.startsWith("ND")==!0&&s.endsWith("01")||parseInt(r)?i.AddPara("IsSetReadonly",0):i.AddPara("IsSetReadonly",1);const S=yield i.DoMethodReturnString("Imp_FromsCopyFrm");return u.emit("reloadForm"),new n(m.Reload,S)}if(t=="ImpEnsFrm"){const e=o,r=new h("BP.WF.HttpHandler.WF_Admin_FoolFormDesigner_ImpExp");r.AddPara("FrmID",s),r.AddPara("EnsName",e);const i=yield r.DoMethodReturnString("Imp_FrmEnsName");return u.emit("reloadForm"),new n(m.Message,i)}if(t=="TableSrc.Tables.Fields"){const e=o,r=I,i=new L("BP.Sys.GroupFields");yield i.Retrieve("FrmID",s);const S=i.getData();let D=0;if(S.length==0){const l=new M;l.Lab="基本信息",l.FrmID=s,l.Idx=1,yield l.DirectInsert(),D=l.OID}else D=S[0].OID;const x=e.split(","),R=r.split(",");for(let l=0;l<x.length;l++){const w=x[l].split("="),g=R[l],f=new N("BP.Sys.MapAttr"),_=`${s}_${w[0]}`;if(f.setPK(_),!(yield f.RetrieveFromDBSources())){const p=f.getData();p.KeyOfEn=w[0],w[1]==="varchar"&&(p.MyDataType=1),w[1]==="int"&&(p.MyDataType=2),p.GroupID=D,p.Idx=l,p.MyPK=_,p.FrmID=s,p.FK_MapData=s,p.Name=g,yield f.Insert()}u.emit("reloadForm")}return new n(m.Message,"导入成功.")}if(t==="ExpExcel"){alert("尚未实现.");return}if(t==="ExpXml"){const e=new N("BP.Sys.MapData",s);yield e.Init();const r=new h("BP.WF.HttpHandler.WF_Admin_CCBPMDesigner");r.AddPara("FK_MapData",s);const i=yield r.DoMethodReturnString("DownFormTemplete");if(i.includes("url@")){const S=i.replace("url@","");return new n(m.GoToUrl,S)}return b(i,e.getData().Name+".xml","xml"),u.emit("reloadForm"),new n(m.DoNothing,"")}})}}export{re as GPN_FrmExpImp};
