
using System;
using System.Data;
using System.Collections;
using BP.DA;
using BP.En;

namespace BP.Sys
{
    /// <summary>
    /// 通用OID实体
    /// </summary>
    public class GEEntityNoName : EntityNoName
    {
        #region 构造函数
        /// <summary>
        /// 转化为类.
        /// </summary>
        /// <returns></returns>
        public override string ToString()
        {
            return this.FrmID;
        }
        public override string ClassID
        {
            get
            {
                return this.FrmID;
            }
        }
        /// <summary>
        /// 主键
        /// </summary>
        public string FrmID = null;
        /// <summary>
        /// 通用OID实体
        /// </summary>
        public GEEntityNoName()
        {
        }
        /// <summary>
        /// 通用OID实体
        /// </summary>
        /// <param name="nodeid">节点ID</param>
        public GEEntityNoName(string fk_mapdata)
        {
            this.FrmID = fk_mapdata;
            this._enMap = null;
        }
        /// <summary>
        /// 
        /// </summary>
        /// <param name="frmID"></param>
        /// <param name="pk"></param>
        public GEEntityNoName(string frmID, object pk)
        {
            this.FrmID = frmID;
            this.PKVal = pk;
            this._enMap = null;
            this.Retrieve();
        }
        #endregion

        #region 构造映射.
        /// <summary>
        /// 重写基类方法
        /// </summary>
        public override Map EnMap
        {
            get
            {
                if (this._enMap != null)
                    return this._enMap;

                if (this.FrmID == null)
                    throw new Exception("没有给[" + this.FrmID + "]值，您不能获取它的Map。");

                this._enMap = BP.Sys.MapData.GenerHisMap(this.FrmID);
                return this._enMap;
            }
        }
        /// <summary>
        /// GEEntityNoNames
        /// </summary>
        public override Entities GetNewEntities
        {
            get
            {
                if (this.FrmID == null)
                    return new GEEntityNoNames();
                return new GEEntityNoNames(this.FrmID);
            }
        }
        #endregion

        /// <summary>
        /// 从另外的一个实体来copy数据.
        /// </summary>
        /// <param name="en"></param>
        public void CopyFromFrm(GEEntityNoName en)
        {
            //先求出来旧的OID.
            string oldNo = this.No;

            //复制主表数据.
            this.Copy(en);
            this.Save();
            this.SetValByKey("No", oldNo);

            //复制从表数据.
            MapDtls dtls = new MapDtls(this.FrmID);

            //被copy的明细集合.
            MapDtls dtlsFrom = new MapDtls(en.FrmID);

            if (dtls.Count != dtlsFrom.Count)
                throw new Exception("@复制的两个表单从表不一致...");

            //序号.
            int i = 0;
            foreach (MapDtl dtl in dtls)
            {
                //删除旧的数据.
                DBAccess.RunSQL("DELETE FROM " + dtl.PTable + " WHERE RefPK='" + oldNo + "'");

                //求对应的Idx的，从表配置.
                MapDtl dtlFrom = dtlsFrom[i] as MapDtl;
                GEDtls ensDtlFrom = new GEDtls(dtlFrom.No);
                ensDtlFrom.Retrieve(GEDtlAttr.RefPK, oldNo);

                //创建一个实体.
                GEDtl dtlEnBlank = new GEDtl(dtl.No);

                // 遍历数据,执行copy.
                foreach (GEDtl enDtlFrom in ensDtlFrom)
                {
                    dtlEnBlank.Copy(enDtlFrom);
                    dtlEnBlank.RefPK = this.No.ToString();
                    dtlEnBlank.SaveAsNew();
                }
                i++;
            }

            //复制附件数据.
            FrmAttachments aths = new FrmAttachments(this.FrmID);
            FrmAttachments athsFrom = new FrmAttachments(en.FrmID);
            foreach (FrmAttachment ath in aths)
            {
                //删除数据,防止copy重复
                DBAccess.RunSQL("DELETE FROM Sys_FrmAttachmentDB WHERE FK_MapData='" + this.FrmID + "' AND RefPKVal='" + this.No + "'");

                foreach (FrmAttachment athFrom in athsFrom)
                {
                    if (athFrom.NoOfObj != ath.NoOfObj)
                        continue;

                    FrmAttachmentDBs athDBsFrom = new FrmAttachmentDBs();
                    athDBsFrom.Retrieve(FrmAttachmentDBAttr.FK_FrmAttachment, athFrom.MyPK, FrmAttachmentDBAttr.RefPKVal, en.No);
                    foreach (FrmAttachmentDB athDBFrom in athDBsFrom)
                    {
                        athDBFrom.setMyPK(DBAccess.GenerGUID());
                        athDBFrom.FrmID = this.FrmID;
                        athDBFrom.FK_FrmAttachment = ath.MyPK;
                        athDBFrom.RefPKVal = this.No;
                        athDBFrom.Insert();
                    }

                }
            }
        }
        /// <summary>
        /// 把当前实体的数据copy到指定的主键数据表里.
        /// </summary>
        /// <param name="oid">指定的主键</param>
        public void CopyToNo(string oid)
        {
            //实例化历史数据表单entity.
            string oidOID = this.No;
            this.No = oid;
            this.Save();

            //复制从表数据.
            MapDtls dtls = new MapDtls(this.FrmID);
            foreach (MapDtl dtl in dtls)
            {
                //删除旧的数据.
                DBAccess.RunSQL("DELETE FROM " + dtl.PTable + " WHERE RefPK='" + this.No + "'");

                GEDtls ensDtl = new GEDtls(dtl.No);

                //   var typeVal = BP.Sys.Base.Glo.GenerRealType( ensDtl.GetNewEntity.EnMap.Attrs, GEDtlAttr.RefPK, this.OID);

                ensDtl.Retrieve(GEDtlAttr.RefPK, oidOID.ToString());

                foreach (GEDtl enDtl in ensDtl)
                {
                    enDtl.RefPK = this.No; 
                    enDtl.InsertAsNew();
                }
            }

            //复制附件数据.
            FrmAttachments aths = new FrmAttachments(this.FrmID);
            foreach (FrmAttachment ath in aths)
            {
                //删除可能存在的新oid数据。
                DBAccess.RunSQL("DELETE FROM Sys_FrmAttachmentDB WHERE FK_MapData='" + this.FrmID + "' AND RefPKVal='" + this.No + "'");

                //找出旧数据.
                FrmAttachmentDBs athDBs = new FrmAttachmentDBs(this.FrmID, oidOID.ToString());
                foreach (FrmAttachmentDB athDB in athDBs)
                {
                    FrmAttachmentDB athDB_N = new FrmAttachmentDB();
                    athDB_N.Copy(athDB);

                    athDB_N.FrmID = this.FrmID;
                    athDB_N.RefPKVal = this.No;

                    if (athDB_N.HisAttachmentUploadType == AttachmentUploadType.Single)
                    {
                        /*如果是单附件.*/
                        athDB_N.setMyPK(athDB_N.FK_FrmAttachment + "_" + this.No);
                        if (athDB_N.IsExits == true)
                            continue;  /*说明上一个节点或者子线程已经copy过了, 但是还有子线程向合流点传递数据的可能，所以不能用break.*/

                        athDB_N.Insert();
                    }
                    else
                    {
                        athDB_N.setMyPK(DBAccess.GenerGUID());
                        athDB_N.Insert();
                    }
                }
            }
        }
        private ArrayList _Dtls = null;
        public ArrayList Dtls
        {
            get
            {
                if (_Dtls == null)
                    _Dtls = new ArrayList();
                return _Dtls;
            }
        }


        #region public 方法
        protected virtual string SerialKey
        {
            get
            {
                return "No";
            }
        }
        /// <summary>
        /// 作为一个新的实体保存。
        /// </summary>
        public void SaveAsNew()
        {
            try
            {
                string oidOID = this.No;

                this.No = this.GenerNewNoByKey("No"); // DBAccess.GenerOIDByGUID(this.SerialKey);
                this.RunSQL(SqlBuilder.Insert(this));

                string ptable = this.EnMap.PhysicsTable;

                //判断有没有DBFile文件.
                if (DBAccess.IsExitsTableCol(ptable, "DBFile") == true)
                {
                    //获取旧数据bye.
                    byte[] val = DBAccess.GetByteFromDB(ptable, "No", oidOID.ToString(), "DBFile");

                    //保存到新记录里面.
                    DBAccess.SaveBytesToDB(val, ptable, "No", this.No.ToString(), "DBFile");
                }

            }
            catch (System.Exception ex)
            {
                this.CheckPhysicsTable();
                throw ex;
            }
        }
        #endregion
    }
    /// <summary>
    /// 通用OID实体s
    /// </summary>
    public class GEEntityNoNames : EntitiesNoName
    {
        #region 重载基类方法
        public override string ToString()
        {
            //if (this.FrmID == null)
            //    throw new Exception("@没有能 FK_MapData 给值。");
            return this.FrmID;
        }
        /// <summary>
        /// 主键
        /// </summary>
        public string FrmID = null;
        #endregion

        #region 方法
        /// <summary>
        /// 得到它的 Entity
        /// </summary>
        public override Entity GetNewEntity
        {
            get
            {
                if (this.FrmID == null)
                    return new GEEntityNoName();
                return new GEEntityNoName(this.FrmID);
            }
        }
        /// <summary>
        /// 通用OID实体ID
        /// </summary>
        public GEEntityNoNames()
        {
        }
        public GEEntityNoNames(string frmID)
        {
            this.FrmID = frmID;
        }
        #endregion

        #region 为了适应自动翻译成java的需要,把实体转换成List.
        /// <summary>
        /// 转化成 java list,C#不能调用.
        /// </summary>
        /// <returns>List</returns>
        public System.Collections.Generic.IList<GEEntityNoName> ToJavaList()
        {
            return (System.Collections.Generic.IList<GEEntityNoName>)this;
        }
        /// <summary>
        /// 转化成list
        /// </summary>
        /// <returns>List</returns>
        public System.Collections.Generic.List<GEEntityNoName> Tolist()
        {
            System.Collections.Generic.List<GEEntityNoName> list = new System.Collections.Generic.List<GEEntityNoName>();
            for (int i = 0; i < this.Count; i++)
            {
                list.Add((GEEntityNoName)this[i]);
            }
            return list;
        }
        #endregion 为了适应自动翻译成java的需要,把实体转换成List.
    }
}
