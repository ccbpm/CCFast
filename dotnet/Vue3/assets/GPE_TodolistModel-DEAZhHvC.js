var a=Object.defineProperty;var d=(n,t,e)=>t in n?a(n,t,{enumerable:!0,configurable:!0,writable:!0,value:e}):n[t]=e;var i=(n,t,e)=>d(n,typeof t!="symbol"?t+"":t,e);import{GPE_TeamleaderConfirm as c}from"./GPE_TeamleaderConfirm-BfBzWTvZ.js";import{GPE_XieZuoOverRole as g}from"./GPE_XieZuoOverRole-BWfWAaqD.js";import{PageBaseGroupEdit as h}from"./PageBaseGroupEdit-JIgqoTiq.js";import{aB as l,aC as p}from"./entry/index-M8VErHPE-1727507756861.js";import{GloComm as m}from"./GloComm-DZ1gELjv.js";import{Node as B,NodeAttr as f}from"./Node-B6HRFhwD.js";import"./Help-D0bDMZWg.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";import"./FrmTrack-0uAZQ3B_.js";import"./DBAccess-CzjFzLoq.js";import"./SFPara-DL_8hzxu.js";import"./SFColumn-Q_PoS_2g.js";import"./EntityNodeID-De9k9loD.js";class _ extends h{constructor(){super("GPE_TodolistModel");i(this,"Desc0",`
  #### 帮助
   -  A发送到B ，B节点上有n个人可以处理。这n个人都可以看到待办，当其中一个人处理后，其他人的待办就消失了。
   -  这样的工作模式属于抢办，这n个人可以同时打开，当一个人发送后，其他人都不能在发送了。
   -  通俗的说，也就是谁抢到了这件工作，就是谁处理的。
   -  抢办模式是一个默认的处理模式。
`);i(this,"Desc1",`
  #### 帮助
   - A发送到B ，B节点上有n个人可以处理。这个n个人都可以去处理，都可以去发送，有最后一个处理人发送到下一个节点上去，这n个人的处理不分顺序。这样的模式我们叫做协作模式。
   - 协作模式通常用在具有审核组件的表单中，每个人都可以填写自己的意见，发表自己的看法，在审核组件里审核每个人在一个节点上只能写入一条数据，也就是说只能表达一次意见。
   - 也经常用在传阅的情况下，必须这些人都读取了，知道了才能向下运动。它与抄送不同的地方是，抄送可以不看，就可以向下运行，但是他必须处理才能向下运行。
   - 通常的协作模式，就是多人都要点击一下发送功能按钮，来确认自己处理了，确认自己同意了或者认可了。
  `);i(this,"Desc2",`
  #### 帮助
 -  A发送到B ，B节点上有n个人可以处理。这个n个人按照设置的先后顺序去处理，在同一个时间点必须有一个人待办，其他人看不到。
 -  通常这样的模式也与审核组件一起使用，每个人按照顺序表达意见后，就发送到下一个节点。
 -  在退回的时候，第一个人退回的是上一个节点，其他人则是依次退回。
 -  一个公文发送给一个部门，该部门下n个人可以接受（注意这是一个节点），按照国内的行政制度，这n个人会从小到达排一个队列，有最小的级别的工作人员审批，然后依次类推，走到最后由最后一个人发送到下一个节点去。这n个人都是该节点的处理人，这n个人的审核步骤是由他们的顺序确定的，所以在设置该节点的接受人时，要按照顺序执行。
`);i(this,"Desc3",`
       
#### 帮助

 - A发送到B ，B节点上有n个人可以处理， ccbpm就会把这个任务放入到任务池，这n个人都可以看到。
 - 其中一个人要处理这件工作需要从任务池里申请取出来放入自己的待办，他取出来以后，其他人都看不到了。
 - 如果该人不想处理这件工作他可以再把这个工作放入任务池，其他人又可以申请了。
 - 更多信息，请参考操作手册。
 #### 运行图
  -  ![输入图片说明](/resource/WF/Admin/AttrNode/TodolistModel/Img/GongXiang.png "屏幕截图.png")
 
 #### 任务池中取出的待办

 -  ![输入图片说明](/resource/WF/Admin/AttrNode/TodolistModel/Img/GongXiang4.png "屏幕截图.png")
 
`);i(this,"Desc4",`

       
#### 帮助

 - 这个模式与协作模式区别在于，如果当前的人员中有一个是组长（领导），他在执行发送的时候就标记这个节点工作结束。
 - 这种模式有如下3中情况：
 - 第一种：接受人员列表里没有领导/组长，这种模式与协作模式是一样的，就是所有的成员都表态（举手）才能向下发送。
 - 第二种：只有一个领导/组长的情况，领导/组长向下发送的时候，标识该节点完成。比如: 当前节点是一个采样节点，采样组有1个组长n个组员，只有组长向下发送的时候才标记次该工作完成。
 - 第三种：有n个领导/组长的情况，n>=1 任何一个领导/组长向下发送的时候标记该流程完成。比如：当前节点的采样任务交给了两个采样组负责，有两个组长n个成员，任何一个组长向下发送，标记该工作结束。


`);this.PageTitle="多人处理规则",this.Btns=[{pageNo:"1",list:["协作完成规则"]},{pageNo:"4",list:["组长确认规则"]}]}Init(){this.entity=new B,this.KeyOfEn=f.TodolistModel,this.AddGroup("A","多人处理规则"),this.Icon="icon-people";let e="@0=不处理(默认);对未处理的人员不做任何操作未处理的人员待办消失.";e+="@1=抄送给未处理的人;向未处理的人员发送抄送消息.",e+="@2=发消息给未处理的人;通过消息机制发送消息给未处理的人员.",this.SingleEnumRadioButon("0","抢办模式","QiangBanSendAfterRole",this.Desc0,"发送后处理规则",e);let o="@0=不删除;正常处理待办工作,不对其他人员的待办做删除处理.";o+="@1=删除同部门的人员(包括主部门+兼职部门);当前工作完成后，该节点上的其他待办人员如果有本部门的人，就把他删除。删除的范围是与当前处理人是同一个部门，包括兼职部门。",o+="@2=删除同角色的人员;如果当前节点是协作模式的节点当前工作完成后，该节点绑定的角色集合，当前操作员的角色集合，与剩余每个人员的角色集合对比如果有交集，该人员就被删除。",o+="@3=删除主部门的人员;当前工作完成后，该节点上的其他待办人员如果有本部门的人，就把他删除。删除的范围是与当前处理人是同一个主部门。",o+="@4=删除兼职部门的人员;当前工作完成后，该节点上的其他待办人员如果有本部门的人，就把他删除.删除的范围是与当前处理人是同一个兼职部门。",this.SingleEnumRadioButon("1","协作模式","GenerWorkerListDelRole",this.Desc1,"协作模式下待办删除规则",o),this.Blank("3","共享模式",this.Desc3),this.Blank("4","协作组长模式",this.Desc4),this.Blank("2","队列模式",this.Desc2)}AfterSave(e,o){if(e==o)throw new Error("Method not implemented.")}BtnClick(e,o,s){if(s=="协作完成规则"){const r=m.UrlGPE(new g,this.RefPKVal);return new l(p.OpenUrlByDrawer90,r)}if(s=="组长确认规则"){const r=m.UrlGPE(new c,this.RefPKVal);return new l(p.OpenUrlByDrawer90,r)}}}export{_ as GPE_TodolistModel};
