package bp.wf.template;

import bp.en.*;
import java.util.*;

/** 
 消息推送
*/
public class PushMsgs extends EntitiesMyPK
{
	/** 
	 消息推送
	*/
	public PushMsgs() {
	}
	/** 
	 消息推送
	 
	 param flowNo 流程编号
	*/
	public PushMsgs(String flowNo) throws Exception {
		//this.RetrieveFromCache(PushMsgAttr.FlowNo, flowNo);
		this.Retrieve(PushMsgAttr.FlowNo, flowNo, null);
	}
	/** 
	 消息推送
	 
	 param nodeid 节点ID
	*/
	public PushMsgs(int nodeid) throws Exception {
		// this.RetrieveFromCache(PushMsgAttr.NodeID, nodeid);
		this.Retrieve(PushMsgAttr.NodeID, nodeid, null);
	}
	/** 
	 得到它的 Entity 
	*/
	@Override
	public Entity getNewEntity() {
		return new PushMsg();
	}


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<PushMsg> ToJavaList() {
		return (java.util.List<PushMsg>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<PushMsg> Tolist()  {
		ArrayList<PushMsg> list = new ArrayList<PushMsg>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((PushMsg)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}