package bp.wf.httphandler;

import bp.da.*;
import bp.difference.StringHelper;
import bp.sys.*;
import bp.en.*; import bp.en.Map;
import bp.*;
import bp.wf.*;
import java.util.*;

/** 
 手机表单设计器
*/
public class WF_Admin_MobileFrmDesigner extends bp.difference.handler.DirectoryPageBase
{
	public final String Default_Init() throws Exception {
		//分组.
		GroupFields gfs = new GroupFields();
		gfs.Retrieve(GroupFieldAttr.FrmID, this.getFrmID(), GroupFieldAttr.Idx);

		MapAttrs attrs = new MapAttrs();
		attrs.Retrieve(MapAttrAttr.FK_MapData, this.getFrmID(), GroupFieldAttr.Idx);

		MapDtls mapDtls = new MapDtls();
		mapDtls.Retrieve(MapDtlAttr.FK_MapData, this.getFrmID(), null);

		FrmAttachments aths = new FrmAttachments(this.getFrmID());

		DataSet ds = new DataSet();

		//分组.
		ds.Tables.add(gfs.ToDataTableField("Sys_GroupFields"));

		//字段.
		ds.Tables.add(attrs.ToDataTableField("Sys_MapAttrs"));

		//从表.
		ds.Tables.add(mapDtls.ToDataTableField("Sys_MapDtls"));

		//附件.
		ds.Tables.add(aths.ToDataTableField("Sys_FrmAttachments"));

		return bp.tools.Json.ToJson(ds);
	}

	public final String Default_Init_bak() throws Exception {
		Nodes nodes = null;


			///#region 获取数据
		MapDatas mapdatas = new MapDatas();
		QueryObject qo = new QueryObject(mapdatas);
		qo.AddWhere(MapDataAttr.No, "Like", this.getFrmID() + "%");
		qo.addOrderBy(MapDataAttr.Idx);
		qo.DoQuery();

		MapAttrs attrs = new MapAttrs();
		attrs.Retrieve(MapDtlAttr.FK_MapData, this.getFrmID(), MapAttrAttr.EditType, 0, GroupFieldAttr.Idx);

		FrmBtns btns = new FrmBtns(this.getFrmID());

		FrmAttachments athMents = new FrmAttachments(this.getFrmID());

		MapDtls dtls = new MapDtls();
		dtls.Retrieve(MapDtlAttr.FK_MapData, this.getFrmID(), GroupFieldAttr.Idx);

		GroupFields groups = new GroupFields();
		groups.Retrieve(GroupFieldAttr.FrmID, this.getFrmID(), GroupFieldAttr.Idx);

			///#endregion

		DataSet ds = new DataSet();

		BindData4Default_Init(mapdatas, attrs, groups, dtls, athMents, btns, nodes, ds);

		//控制页面按钮需要的
		MapDtl tdtl = new MapDtl();
		tdtl.setNo(this.getFrmID());
		if (tdtl.RetrieveFromDBSources() == 1)
		{
			ds.Tables.add(tdtl.ToDataTableField("tdtl"));
		}

		return bp.tools.Json.ToJson(ds);
	}

	private void BindData4Default_Init(MapDatas mapdatas, MapAttrs attrs, GroupFields groups, MapDtls dtls, FrmAttachments athMents, FrmBtns btns, Nodes nodes, DataSet ds) throws Exception {
		Object tempVar = mapdatas.GetEntityByKey(this.getFrmID());
		MapData mapdata = tempVar instanceof MapData ? (MapData)tempVar : null;
		DataTable dtAttrs = attrs.ToDataTableField("dtAttrs");
		DataTable dtDtls = dtls.ToDataTableField("dtDtls");
		DataTable dtGroups = groups.ToDataTableField("dtGroups");
		DataTable dtNoGroupAttrs = null;
		DataRow[] rows_Attrs = null;
		int idx_Attr = 1;
		int gidx = 1;
		GroupField group = null;

		if (mapdata != null)
		{
			///#region 一、面板1、 分组数据+未分组数据
			///#region A、构建数据dtNoGroupAttrs，这个放在前面
			//检索全部字段，查找出没有分组或分组信息不正确的字段，存入"无分组”集合
			dtNoGroupAttrs = dtAttrs.clone();

			for (DataRow dr : dtAttrs.Rows)
			{
				if (IsExistInDataRowArray(dtGroups.Rows, GroupFieldAttr.OID, dr.getValue(MapAttrAttr.GroupID)) == false)
				{
					dtNoGroupAttrs.Rows.add(dr);
				}
			}

				///#endregion


				///#region B、构建数据dtGroups，这个放在后面(！！涉及更新数据库)

				///#region 如果没有，则创建分组（1.明细2.多附件3.按钮）
			//01、未分组明细表,自动创建一个
			for (MapDtl mapDtl : dtls.ToJavaList())
			{
				if (GetGroupID(mapDtl.getNo(), groups) == 0)
				{
					group = new GroupField();
					group.setLab(mapDtl.getName());
					group.setFrmID(mapDtl.getFrmID());
					group.setCtrlType(GroupCtrlType.Dtl);
					group.setCtrlID(mapDtl.getNo());
					group.Insert();

					groups.AddEntity(group);
				}
			}
			//02、未分组多附件自动分配一个
			for (FrmAttachment athMent : athMents.ToJavaList())
			{
				if (GetGroupID(athMent.getMyPK(), groups) == 0)
				{
					group = new GroupField();
					group.setLab(athMent.getName());
					group.setEnName(athMent.getFrmID());
					group.setCtrlType(GroupCtrlType.Ath);
					group.setCtrlID(athMent.getMyPK());
					group.Insert();

					athMent.setGroupID(group.getOID());
					athMent.Update();

					groups.AddEntity(group);
				}
			}

			//03、未分组按钮自动创建一个
			for (FrmBtn fbtn : btns.ToJavaList())
			{
				if (GetGroupID(fbtn.getMyPK(), groups) == 0)
				{
					group = new GroupField();
					group.setLab(fbtn.getLab());
					group.setFrmID(fbtn.getFrmID());
					group.setCtrlType(GroupCtrlType.Btn);
					group.setCtrlID(fbtn.getMyPK());
					group.Insert();

					fbtn.setGroupID(group.getOID());
					fbtn.Update();

					groups.AddEntity(group);
				}
			}

				///#endregion

			dtGroups = groups.ToDataTableField("dtGroups");

				///#endregion



				///#endregion


				///#region 三、其他。如果是明细表的字段排序，则增加"返回”按钮；否则增加"复制排序"按钮,2016-03-21
			DataTable isDtl = new DataTable();
			isDtl.Columns.Add("tdDtl", Integer.class);
			isDtl.Columns.Add("FK_MapData", String.class);
			isDtl.Columns.Add("No", String.class);
			isDtl.TableName = "TRDtl";

			DataRow tddr = isDtl.NewRow();

			MapDtl tdtl = new MapDtl();
			tdtl.setNo(this.getFrmID());
			if (tdtl.RetrieveFromDBSources() == 1)
			{
				tddr.setValue("tdDtl", 1);
				tddr.setValue("FK_MapData", tdtl.getFrmID());
				tddr.setValue("No", tdtl.getNo());
			}
			else
			{
				tddr.setValue("tdDtl", 0);
				tddr.setValue("FK_MapData", this.getFrmID());
				tddr.setValue("No", tdtl.getNo());
			}
			isDtl.Rows.add(tddr);

				///#endregion


				///#region 增加节点信息
			if (DataType.IsNullOrEmpty(this.getFlowNo()) == false)
			{
				nodes = new Nodes();
				nodes.Retrieve(bp.wf.template.NodeAttr.FK_Flow, this.getFlowNo(), bp.wf.template.NodeAttr.Step);

				if (nodes.size() == 0)
				{
					String nodeid = this.getFrmID().replace("ND", "");
					String flowno = "";

					if (nodeid.length() > 2)
					{
						flowno = StringHelper.padLeft(nodeid.substring(0, nodeid.length() - 2), 3, '0');
						nodes.Retrieve(bp.wf.template.NodeAttr.FK_Flow, flowno, bp.wf.template.NodeAttr.Step);
					}
				}
				DataTable dtNodes = nodes.ToDataTableField("dtNodes");
				dtNodes.TableName = "dtNodes";
				ds.Tables.add(dtNodes);
			}

				///#endregion

			ds.Tables.add(mapdatas.ToDataTableField("mapdatas"));
			dtGroups.TableName = "dtGroups";
			ds.Tables.add(dtGroups);
			dtNoGroupAttrs.TableName = "dtNoGroupAttrs";
			ds.Tables.add(dtNoGroupAttrs);
			dtAttrs.TableName = "dtAttrs";
			ds.Tables.add(dtAttrs);
			dtDtls.TableName = "dtDtls";
			ds.Tables.add(dtDtls);
			ds.Tables.add(athMents.ToDataTableField("athMents"));
			ds.Tables.add(btns.ToDataTableField("btns"));
			ds.Tables.add(isDtl);
			//ds.Tables.add(nodes.ToDataTableField("nodes"));
		}
	}
	private long GetGroupID(String ctrlID, GroupFields gfs) throws Exception {
		Object tempVar = gfs.GetEntityByKey(GroupFieldAttr.CtrlID, ctrlID);
		GroupField gf = tempVar instanceof GroupField ? (GroupField)tempVar : null;
		return gf == null ? 0 : gf.getOID();
	}
	/** 
	 判断在DataRow数组中，是否存在指定列指定值的行
	 
	 @param rows DataRow数组
	 @param field 指定列名
	 @param value 指定值
	 @return 
	*/
	private boolean IsExistInDataRowArray(DataRowCollection rows, String field, Object value)
	{
		for (DataRow row : rows)
		{
			int rw = Integer.parseInt(row.get(field).toString());
			if (rw == Integer.parseInt(value.toString()))
			{
				return true;
			}
		}
		return false;
	}
	/** 
	 手机表单设计器
	*/
	public WF_Admin_MobileFrmDesigner()
	{
	}
	/** 
	 保存需要在手机端表单显示的字段
	 
	 @return 
	*/
	public final String Default_From_Save()
	{
		//获取需要显示的字段集合
		String atts = this.GetRequestVal("attrs");
		try
		{
			MapAttrs mattrs = new MapAttrs(this.getFrmID());
			MapAttr att = null;
			//更新每个字段的显示属性
			for (MapAttr attr : mattrs.ToJavaList())
			{
				Object tempVar = mattrs.GetEntityByKey(MapAttrAttr.FK_MapData, this.getFrmID(), MapAttrAttr.KeyOfEn, attr.getKeyOfEn());
				att = tempVar instanceof MapAttr ? (MapAttr)tempVar : null;
				if (atts.contains("," + attr.getKeyOfEn() + ","))
				{
					att.setItIsEnableInAPP(true);
				}
				else
				{
					att.setItIsEnableInAPP(false);
				}
				att.Update();
			}
			//获取附件
			FrmAttachments aths = new FrmAttachments();
			aths.Retrieve(FrmAttachmentAttr.FK_MapData, this.getFrmID(), FrmAttachmentAttr.FK_Node, 0, null);
			for (FrmAttachment ath : aths.ToJavaList())
			{
				if (atts.contains("," + ath.getMyPK() + ",") == true)
				{
					ath.SetPara("IsShowMobile", 1);
				}
				else
				{
					ath.SetPara("IsShowMobile", 0);
				}
				ath.Update();
			}
			return "保存成功！";
		}
		catch (Exception ex)
		{
			return "err@" + ex.getMessage().toString();
		}
	}
	/** 
	 将分组、字段排序复制到其他节点
	 
	 @return 
	*/
	public final String Default_Copy()
	{
		try
		{
			String[] nodeids = this.GetRequestVal("NodeIDs").split("[,]", -1);

			MapDatas mapdatas = new MapDatas();
			QueryObject obj = new QueryObject(mapdatas);
			obj.AddWhere(MapDataAttr.No, "Like", this.getFrmID() + "%");
			obj.addOrderBy(MapDataAttr.Idx);
			obj.DoQuery();

			MapAttrs mattrs = new MapAttrs();
			obj = new QueryObject(mattrs);
			obj.AddWhere(MapAttrAttr.FK_MapData, this.getFrmID());
			obj.addAnd();
			obj.AddWhere(MapAttrAttr.UIVisible, true);
			obj.addOrderBy(MapAttrAttr.GroupID, MapAttrAttr.Idx);
			obj.DoQuery();

			FrmBtns btns = new FrmBtns(this.getFrmID());
			FrmAttachments athMents = new FrmAttachments(this.getFrmID());
			MapDtls dtls = new MapDtls(this.getFrmID());

			GroupFields groups = new GroupFields();
			obj = new QueryObject(groups);
			obj.AddWhere(GroupFieldAttr.FrmID, this.getFrmID());
			obj.addOrderBy(GroupFieldAttr.Idx);
			obj.DoQuery();

			String tmd = null;
			GroupField group = null;
			MapDatas tmapdatas = null;
			MapAttrs tattrs = null, oattrs = null, tarattrs = null;
			GroupFields tgroups = null, ogroups = null, targroups = null;
			MapDtls tdtls = null;
			MapData tmapdata = null;
			MapAttr tattr = null;
			GroupField tgrp = null;
			MapDtl tdtl = null;
			int maxGrpIdx = 0; //当前最大分组排序号
			int maxAttrIdx = 0; //当前最大字段排序号
			int maxDtlIdx = 0; //当前最大明细表排序号
			ArrayList<String> idxGrps = new ArrayList<String>(); //复制过的分组名称集合
			ArrayList<String> idxAttrs = new ArrayList<String>(); //复制过的字段编号集合
			ArrayList<String> idxDtls = new ArrayList<String>(); //复制过的明细表编号集合

			for (String nodeid : nodeids)
			{
				tmd = "ND" + nodeid;


					///#region 获取数据
				tmapdatas = new MapDatas();
				QueryObject qo = new QueryObject(tmapdatas);
				qo.AddWhere(MapDataAttr.No, "Like", tmd + "%");
				qo.addOrderBy(MapDataAttr.Idx);
				qo.DoQuery();

				tattrs = new MapAttrs();
				qo = new QueryObject(tattrs);
				qo.AddWhere(MapAttrAttr.FK_MapData, tmd);
				qo.addAnd();
				qo.AddWhere(MapAttrAttr.UIVisible, true);
				qo.addOrderBy(MapAttrAttr.GroupID, MapAttrAttr.Idx);
				qo.DoQuery();

				tgroups = new GroupFields();
				qo = new QueryObject(tgroups);
				qo.AddWhere(GroupFieldAttr.FrmID, tmd);
				qo.addOrderBy(GroupFieldAttr.Idx);
				qo.DoQuery();

				tdtls = new MapDtls();
				qo = new QueryObject(tdtls);
				qo.AddWhere(MapDtlAttr.FK_MapData, tmd);
				qo.addAnd();
				qo.AddWhere(MapDtlAttr.IsView, true);
				//qo.addOrderBy(MapDtlAttr.RowIdx);
				qo.DoQuery();

					///#endregion


					///#region 复制排序逻辑


					///#region //分组排序复制
				for (GroupField grp : groups.ToJavaList())
				{
					//通过分组名称来确定是同一个组，同一个组在不同的节点分组编号是不一样的
					Object tempVar = tgroups.GetEntityByKey(GroupFieldAttr.Lab, grp.getLab());
					tgrp = tempVar instanceof GroupField ? (GroupField)tempVar : null;
					if (tgrp == null)
					{
						continue;
					}

					tgrp.setIdx(grp.getIdx());
					tgrp.DirectUpdate();

					maxGrpIdx = Math.max(grp.getIdx(), maxGrpIdx);
					idxGrps.add(grp.getLab());
				}

				for (GroupField grp : tgroups.ToJavaList())
				{
					if (idxGrps.contains(grp.getLab()))
					{
						continue;
					}
					maxGrpIdx = maxGrpIdx + 1;
					grp.setIdx(maxGrpIdx);
					grp.DirectUpdate();
				}

					///#endregion


					///#region //字段排序复制
				for (MapAttr attr : mattrs.ToJavaList())
				{
					//排除主键
					if (attr.getItIsPK() == true)
					{
						continue;
					}

					Object tempVar2 = tattrs.GetEntityByKey(MapAttrAttr.KeyOfEn, attr.getKeyOfEn());
					tattr = tempVar2 instanceof MapAttr ? (MapAttr)tempVar2 : null;
					if (tattr == null)
					{
						continue;
					}

					Object tempVar3 = groups.GetEntityByKey(GroupFieldAttr.OID, attr.getGroupID());
					group = tempVar3 instanceof GroupField ? (GroupField)tempVar3 : null;

					//比对字段的分组是否一致，不一致则更新一致
					if (group == null)
					{
						//源字段分组为空，则目标字段分组置为0
						tattr.setGroupID(0);
					}
					else
					{
						//此处要判断目标节点中是否已经创建了这个源字段所属分组，如果没有创建，则要自动创建
						Object tempVar4 = tgroups.GetEntityByKey(GroupFieldAttr.Lab, group.getLab());
						tgrp = tempVar4 instanceof GroupField ? (GroupField)tempVar4 : null;

						if (tgrp == null)
						{
							tgrp = new GroupField();
							tgrp.setLab(group.getLab());
							tgrp.setFrmID(tmd);
							tgrp.setIdx(group.getIdx());
							tgrp.Insert();
							tgroups.AddEntity(tgrp);

							tattr.setGroupID(tgrp.getOID());
						}
						else
						{
							if (tgrp.getOID() != tattr.getGroupID())
							{
								tattr.setGroupID(tgrp.getOID());
							}
						}
					}

					tattr.setIdx(attr.getIdx());
					tattr.DirectUpdate();
					maxAttrIdx = Math.max(attr.getIdx(), maxAttrIdx);
					idxAttrs.add(attr.getKeyOfEn());
				}

				for (MapAttr attr : mattrs.ToJavaList())
				{
					//排除主键
					if (attr.getItIsPK() == true)
					{
						continue;
					}
					if (idxAttrs.contains(attr.getKeyOfEn()))
					{
						continue;
					}
					maxAttrIdx = maxAttrIdx + 1;
					attr.setIdx(maxAttrIdx);
					attr.DirectUpdate();
				}

					///#endregion


					///#region //明细表排序复制
				String dtlIdx = "";
				GroupField tgroup = null;
				int groupidx = 0;
				int tgroupidx = 0;

				for (MapDtl dtl : dtls.ToJavaList())
				{
					dtlIdx = dtl.getNo().replace(dtl.getFrmID() + "Dtl", "");
					Object tempVar5 = tdtls.GetEntityByKey(MapDtlAttr.No, tmd + "Dtl" + dtlIdx);
					tdtl = tempVar5 instanceof MapDtl ? (MapDtl)tempVar5 : null;

					if (tdtl == null)
					{
						continue;
					}

					//判断目标明细表是否有分组，没有分组，则创建分组
					tgroup = GetGroup(tdtl.getNo(), tgroups);
					tgroupidx = tgroup == null ? 0 : tgroup.getIdx();
					group = GetGroup(dtl.getNo(), groups);
					groupidx = group == null ? 0 : group.getIdx();

					if (tgroup == null)
					{
						group = new GroupField();
						group.setLab(tdtl.getName());
						group.setFrmID(tdtl.getFrmID());
						group.setCtrlType(GroupCtrlType.Dtl);
						group.setCtrlID(tdtl.getNo());
						group.setIdx(groupidx);
						group.Insert();

						tgroupidx = groupidx;
						tgroups.AddEntity(group);
					}


						///#region 1.明细表排序
					if (tgroupidx != groupidx && group != null)
					{
						tgroup.setIdx(groupidx);
						tgroup.DirectUpdate();

						tgroupidx = groupidx;
						Object tempVar6 = tmapdatas.GetEntityByKey(MapDataAttr.No, tdtl.getNo());
						tmapdata = tempVar6 instanceof MapData ? (MapData)tempVar6 : null;
						if (tmapdata != null)
						{
							tmapdata.setIdx(tgroup.getIdx());
							tmapdata.DirectUpdate();
						}
					}

					maxDtlIdx = Math.max(tgroupidx, maxDtlIdx);
					idxDtls.add(dtl.getNo());

						///#endregion


						///#region 2.获取源节点明细表中的字段分组、字段信息
					oattrs = new MapAttrs();
					qo = new QueryObject(oattrs);
					qo.AddWhere(MapAttrAttr.FK_MapData, dtl.getNo());
					qo.addAnd();
					qo.AddWhere(MapAttrAttr.UIVisible, true);
					qo.addOrderBy(MapAttrAttr.GroupID, MapAttrAttr.Idx);
					qo.DoQuery();

					ogroups = new GroupFields();
					qo = new QueryObject(ogroups);
					qo.AddWhere(GroupFieldAttr.FrmID, dtl.getNo());
					qo.addOrderBy(GroupFieldAttr.Idx);
					qo.DoQuery();

						///#endregion


						///#region 3.获取目标节点明细表中的字段分组、字段信息
					tarattrs = new MapAttrs();
					qo = new QueryObject(tarattrs);
					qo.AddWhere(MapAttrAttr.FK_MapData, tdtl.getNo());
					qo.addAnd();
					qo.AddWhere(MapAttrAttr.UIVisible, true);
					qo.addOrderBy(MapAttrAttr.GroupID, MapAttrAttr.Idx);
					qo.DoQuery();

					targroups = new GroupFields();
					qo = new QueryObject(targroups);
					qo.AddWhere(GroupFieldAttr.FrmID, tdtl.getNo());
					qo.addOrderBy(GroupFieldAttr.Idx);
					qo.DoQuery();

						///#endregion


						///#region 4.明细表字段分组排序
					maxGrpIdx = 0;
					idxGrps = new ArrayList<String>();

					for (GroupField grp : ogroups.ToJavaList())
					{
						//通过分组名称来确定是同一个组，同一个组在不同的节点分组编号是不一样的
						Object tempVar7 = targroups.GetEntityByKey(GroupFieldAttr.Lab, grp.getLab());
						tgrp = tempVar7 instanceof GroupField ? (GroupField)tempVar7 : null;
						if (tgrp == null)
						{
							continue;
						}

						tgrp.setIdx(grp.getIdx());
						tgrp.DirectUpdate();

						maxGrpIdx = Math.max(grp.getIdx(), maxGrpIdx);
						idxGrps.add(grp.getLab());
					}

					for (GroupField grp : targroups.ToJavaList())
					{
						if (idxGrps.contains(grp.getLab()))
						{
							continue;
						}
						maxGrpIdx = maxGrpIdx + 1;
						grp.setIdx(maxGrpIdx);
						grp.DirectUpdate();
					}

						///#endregion


						///#region 5.明细表字段排序
					maxAttrIdx = 0;
					idxAttrs = new ArrayList<String>();

					for (MapAttr attr : oattrs.ToJavaList())
					{
						Object tempVar8 = tarattrs.GetEntityByKey(MapAttrAttr.KeyOfEn, attr.getKeyOfEn());
						tattr = tempVar8 instanceof MapAttr ? (MapAttr)tempVar8 : null;
						if (tattr == null)
						{
							continue;
						}

						Object tempVar9 = ogroups.GetEntityByKey(GroupFieldAttr.OID, attr.getGroupID());
						group = tempVar9 instanceof GroupField ? (GroupField)tempVar9 : null;

						//比对字段的分组是否一致，不一致则更新一致
						if (group == null)
						{
							//源字段分组为空，则目标字段分组置为0
							tattr.setGroupID(0);
						}
						else
						{
							//此处要判断目标节点中是否已经创建了这个源字段所属分组，如果没有创建，则要自动创建
							Object tempVar10 = targroups.GetEntityByKey(GroupFieldAttr.Lab, group.getLab());
							tgrp = tempVar10 instanceof GroupField ? (GroupField)tempVar10 : null;

							if (tgrp == null)
							{
								tgrp = new GroupField();
								tgrp.setLab(group.getLab());
								tgrp.setEnName(tdtl.getNo());
								tgrp.setIdx(group.getIdx());
								tgrp.Insert();
								targroups.AddEntity(tgrp);

								tattr.setGroupID(tgrp.getOID());
							}
							else
							{
								if (tgrp.getOID() != tattr.getGroupID())
								{
									tattr.setGroupID(tgrp.getOID());
								}
							}
						}

						tattr.setIdx(attr.getIdx());
						tattr.DirectUpdate();
						maxAttrIdx = Math.max(attr.getIdx(), maxAttrIdx);
						idxAttrs.add(attr.getKeyOfEn());
					}

					for (MapAttr attr : tarattrs.ToJavaList())
					{
						if (idxAttrs.contains(attr.getKeyOfEn()))
						{
							continue;
						}
						maxAttrIdx = maxAttrIdx + 1;
						attr.setIdx(maxAttrIdx);
						attr.DirectUpdate();
					}

						///#endregion
				}

				//确定目标节点中，源节点没有的明细表的排序
				for (MapDtl dtl : tdtls.ToJavaList())
				{
					if (idxDtls.contains(dtl.getNo()))
					{
						continue;
					}

					maxDtlIdx = maxDtlIdx + 1;
					tgroup = GetGroup(dtl.getNo(), tgroups);

					if (tgroup == null)
					{
						tgroup = new GroupField();
						tgroup.setLab(tdtl.getName());
						tgroup.setFrmID(tdtl.getFrmID());
						tgroup.setCtrlType(GroupCtrlType.Dtl);
						tgroup.setCtrlID(tdtl.getNo());
						tgroup.setIdx(maxDtlIdx);
						tgroup.Insert();

						tgroups.AddEntity(group);
					}

					if (tgroup.getIdx() != maxDtlIdx)
					{
						tgroup.setIdx(maxDtlIdx);
						tgroup.DirectUpdate();
					}

					Object tempVar11 = tmapdatas.GetEntityByKey(MapDataAttr.No, dtl.getNo());
					tmapdata = tempVar11 instanceof MapData ? (MapData)tempVar11 : null;
					if (tmapdata != null)
					{
						tmapdata.setIdx(maxDtlIdx);
						tmapdata.DirectUpdate();
					}
				}

					///#endregion


					///#endregion

			}
			return "复制成功！";
		}
		catch (Exception ex)
		{
			return "err@" + ex.getMessage().toString();
		}
	}

	private GroupField GetGroup(String ctrlID, GroupFields gfs) throws Exception {
		Object tempVar = gfs.GetEntityByKey(GroupFieldAttr.CtrlID, ctrlID);
		return tempVar instanceof GroupField ? (GroupField)tempVar : null;
	}
	public final String Default_Save() throws Exception {
		Node nd = new Node(this.getNodeID());

		MapData md = new MapData("ND" + this.getNodeID());

		//用户选择的表单类型.
		String selectFModel = this.GetValFromFrmByKey("FrmS");

		//使用ccbpm内置的节点表单
		if (Objects.equals(selectFModel, "DefFrm"))
		{
			String frmModel = this.GetValFromFrmByKey("RB_Frm");
			if (Objects.equals(frmModel, "0"))
			{
				nd.setFormType(NodeFormType.Develop);
				nd.DirectUpdate();

				md.setHisFrmType(FrmType.Develop);
				md.Update();
			}
			else
			{
				nd.setFormType(NodeFormType.FoolForm);
				nd.DirectUpdate();

				md.setHisFrmType(FrmType.FoolForm);
				md.Update();
			}

			String refFrm = this.GetValFromFrmByKey("RefFrm");

			if (Objects.equals(refFrm, "0"))
			{
				nd.setNodeFrmID( "");
				nd.DirectUpdate();
			}

			if (Objects.equals(refFrm, "1"))
			{
				nd.setNodeFrmID( "ND" + this.GetValFromFrmByKey("DDL_Frm"));
				nd.DirectUpdate();
			}
		}

		//使用傻瓜轨迹表单模式.
		if (Objects.equals(selectFModel, "FoolTruck"))
		{
			nd.setFormType(NodeFormType.FoolTruck);
			nd.DirectUpdate();

			md.setHisFrmType(FrmType.FoolForm); //同时更新表单表住表.
			md.Update();
		}

		//使用嵌入式表单
		if (Objects.equals(selectFModel, "SelfForm"))
		{
			nd.setFormType(NodeFormType.SelfForm);
			nd.setFormUrl(this.GetValFromFrmByKey("TB_CustomURL"));
			nd.DirectUpdate();

			md.setHisFrmType(FrmType.Url); //同时更新表单表住表.
			md.setUrlExt(this.GetValFromFrmByKey("TB_CustomURL"));
			md.Update();
		}
		//使用SDK表单
		if (Objects.equals(selectFModel, "SDKForm"))
		{
			nd.setFormType(NodeFormType.SDKForm);
			nd.setFormUrl(this.GetValFromFrmByKey("TB_FormURL"));
			nd.DirectUpdate();

			md.setHisFrmType(FrmType.Url);
			md.setUrlExt(this.GetValFromFrmByKey("TB_FormURL"));
			md.Update();

		}
		//绑定多表单
		if (Objects.equals(selectFModel, "SheetTree"))
		{

			String sheetTreeModel = this.GetValFromFrmByKey("SheetTreeModel");

			if (Objects.equals(sheetTreeModel, "0"))
			{
				nd.setFormType(NodeFormType.SheetTree);
				nd.DirectUpdate();

				md.setHisFrmType(FrmType.FoolForm); //同时更新表单表住表.
				md.Update();
			}
			else
			{
				nd.setFormType(NodeFormType.DisableIt);
				nd.DirectUpdate();

				md.setHisFrmType(FrmType.FoolForm); //同时更新表单表住表.
				md.Update();
			}
		}
		return "保存成功...";
	}
	/** 
	 重置字段顺序
	 
	 @return 
	*/
	public final String Default_ReSet()
	{
		try
		{
			MapAttrs mattrs = new MapAttrs();
			QueryObject qo = new QueryObject(mattrs);
			qo.AddWhere(MapAttrAttr.FK_MapData, this.getFrmID()); //添加查询条件
			qo.addAnd();
			qo.AddWhere(MapAttrAttr.UIVisible, true);
			//qo.addOrderBy(MapAttrAttr.Y, MapAttrAttr.X);
			qo.DoQuery(); //执行查询
			int rowIdx = 0;
			//执行更新
			for (MapAttr mapAttr : mattrs.ToJavaList())
			{
				mapAttr.setIdx(rowIdx);
				mapAttr.DirectUpdate();
				rowIdx++;
			}

			return "重置成功！";
		}
		catch (Exception ex)
		{
			return "err@" + ex.getMessage().toString();
		}
	}
}
