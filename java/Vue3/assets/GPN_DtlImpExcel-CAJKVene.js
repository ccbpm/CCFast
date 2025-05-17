var D=Object.defineProperty;var u=(s,r,e)=>r in s?D(s,r,{enumerable:!0,configurable:!0,writable:!0,value:e}):s[r]=e;var c=(s,r,e)=>u(s,typeof r!="symbol"?r+"":r,e);var p=(s,r,e)=>new Promise((l,n)=>{var i=a=>{try{o(e.next(a))}catch(t){n(t)}},m=a=>{try{o(e.throw(a))}catch(t){n(t)}},o=a=>a.done?l(a.value):Promise.resolve(a.value).then(i,m);o((e=e.apply(s,r)).next())});import{b5 as I,m as P,G as _,l as g,H as A}from"./entry/index-C6uBgOW5-1730430676707.js";import"./vue-BXIlYw1E.js";import"./antd-Dd9L3uAF.js";class G extends I{constructor(){super("GPN_DtlImpExcel");c(this,"Desc0",`
  #### 帮助
   1. 下载模板，如遇到下载出错的情况，请联系管理员制作模板.
    1.1. 模板制作规则：将从表的列名复制到Excel的第一行。
    ![输入图片说明](/resource/WF/Admin/FrmLogic/MapDtl/EditModel/DtlImg.png "屏幕截图.png")
    1.2. 把制作好的模板文件放入后台的 \\DataUesr\\TempleteOfImp 目录。
    ![输入图片说明](/resource/WF/Admin/FrmLogic/MapDtl/EditModel/FilePath.png "屏幕截图.png") 
   2. 输入数据.
   3. 上传excel文件，执行导入.
 
  `);this.PageTitle="导入"}Init(){return p(this,null,function*(){const e=this.params.query.EnsName;this.AddGroup("A","导入"),this.FileUpload("0","清空方式导入","请上传符合格式的Excel文件.",this.Desc0);const{VITE_GLOB_API_URL:l}=P(),n=l+"/DataUser/TempleteOfImp/"+e+".xls";this.AddGoToUrl("DownTemplate","下载模板",n)})}GenerSorts(){return p(this,null,function*(){return Promise.resolve([])})}Save_TextBox_X(e,l,n,i,m){return p(this,null,function*(){if(e=="DownTemplate")return;const o=this.params.dtlInfo.No,a=this.params.query.WorkID,t=new A("BP.WF.HttpHandler.WF_CCForm");t.AddFile(this.UploadFile),t.AddPara("EnsName",o),t.AddPara("PageType","Vue3"),t.AddPara("FK_MapData",o),t.AddPara("WorkID",a),t.AddPara("DDL_ImpWay",e);const d=yield t.DoMethodReturnString("DtlImpByExcel_Imp");return new _(g.Message,(d==null?void 0:d.Msg)||"导入成功")})}}export{G as GPN_DtlImpExcel};
