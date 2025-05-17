package bp.demo;

import bp.port.Emp;
import bp.wf.Flow;
import bp.wf.template.FlowSort;

public class DemoBp {
    /**
     Entity 的基本应用.
     */
    public final String EntityBaseApp() throws Exception {
        ///#region  直接插入一条数据.
        Emp emp = new Emp();
        emp.CheckPhysicsTable();
        /*  检查物理表是否与Map一致
         *  1，如果没有这个物理表则创建。
         *  2，如果缺少字段则创建。
         *  3，如果字段类型不一直则删除创建，比如原来是int类型现在map修改成string类型。
         *  4，map字段减少则不处理。
         *  5，手工的向物理表中增加的字段则不处理。
         *  6，数据源是视图字段不匹配则创建失败。
         * */
        emp.setNo("zhangsan");
        emp.setName("张三");
        emp.setDeptNo("01");
        emp.Insert(); // 如果主键重复要抛异常。
        ///#endregion  直接插入一条数据.

        ///#region  保存的方式插入一条数据.
        emp = new Emp();
        emp.setNo("zhangsan");
        emp.setName("张三");
        emp.setDeptNo("01");
        emp.Save(); // 如果主键重复直接更新，不会抛出异常。
        ///#endregion  保存的方式插入一条数据.

        ///#region  其他方法.
        Emp myEmp2 = new Emp();
        myEmp2.setNo("zhangsan");

        //检查主键数据是否存在 ?
        boolean isExit = myEmp2.getIsExits();
        if (myEmp2.RetrieveFromDBSources() == 0)
        {
            /*说明没有查询到数据。*/
        }
        ///#endregion.
        ///#region  数据复制.
        /*
         * 如果一个实体与另外的一个实体两者的属性大致相同，就可以执行copy.
         *  比如：在创建人员时，张三与李四两者只是编号与名称不同，只是改变不同的属性就可以执行相关的业务操作。
         */
        Emp emp1 = new Emp("zhangsan");
        emp = new Emp();
        emp.Copy(emp1); // 同实体copy, 不同的实体也可以实现copy.
        emp.setNo("lisi");
        emp.setName("李四");
        emp.Insert();

        // copy 在业务逻辑上会经常应用，比如: 在一个流程中A节点表单与B节点表单字段大致相同，ccflow就是采用的copy方式处理。
        ///#endregion  数据复制.

        ///#region 单个实体查询.
        String msg = ""; // 查询这条数据.
        Emp myEmp = new Emp();
        myEmp.setNo("zhangsan");
        if (myEmp.RetrieveFromDBSources() == 0) // RetrieveFromDBSources() 返回来的是查询数量.
        {
            //this.Response.Write("没有查询到编号等于zhangsan的人员记录.");
            msg = "没有查询到编号等于zhangsan的人员记录.";
        }
        else
        {
            msg = "";
            msg += "<BR>编号:" + myEmp.getNo();
            msg += "<BR>名称:" + myEmp.getName();
            msg += "<BR>密码:" + myEmp.getPass();
            msg += "<BR>部门编号:" + myEmp.getDeptNo();
            msg += "<BR>部门名称:" + myEmp.getDeptText();
        }

        myEmp = new Emp();
        myEmp.setNo("zhangsan");
        myEmp.Retrieve(); // 执行查询，如果查询不到则要抛出异常。

        msg = "";
        msg += "<BR>编号:" + myEmp.getNo();
        msg += "<BR>名称:" + myEmp.getName();
        msg += "<BR>密码:" + myEmp.getPass();
        msg += "<BR>部门编号:" + myEmp.getDeptNo();
        msg += "<BR>部门名称:" + myEmp.getDeptText();

        ///#endregion 查询.

        ///#region 两种方式的删除。
        // 删除操作。
        emp = new Emp();
        emp.setNo("zhangsan");
        int delNum = emp.Delete(); // 执行删除。
        if (delNum == 0)
            return "删除 zhangsan 失败.";
        if (delNum == 1)
            return "删除 zhangsan 成功..";
        if (delNum > 1)
            return "不应该出现的异常。";
        // 初试化实例后，执行删除，这种方式要执行两个sql.
        emp = new Emp("abc");
        emp.Delete();

        ///#endregion 两种方式的删除。

        ///#region 更新。
        emp = new Emp("zhangyifan"); // 事例化它.
        emp.setName("张一帆123"); //改变属性.
        emp.Update(); // 更新它，这个时间BP将会把所有的属性都要执行更新，UPDATA 语句涉及到各个列。

        emp = new Emp("fuhui"); // 事例化它.
        emp.Update("Name", "福慧123"); //仅仅更新这一个属性。.UPDATA 语句涉及到Name列。
        ///#endregion 更新。
        return msg;
    }
    /**
     展示EnttiyNo自动编号
     */
    public final String EnttiyNo() throws Exception {
        // 创建一个空的实体.
        Student en = new Student();

        // 给各个属性赋值，但是不要给编号赋值.
        en.setName("张三");
        en.setFJ_BanJi("001");
        en.setAge(19);
        en.setXB(1);
        en.setTel("0531-82374939");
        en.setAddr("山东.济南.高新区");
        en.Insert(); //这里会自动给该学生编号，从0001开始，编号规则打开该类的Map.

        String xuehao = en.getNo();
        String msg = "信息已经加入,该学生的学号为:" + xuehao;

        //查询出来该实体。
        Student myen = new Student(xuehao);
        msg+=",学生姓名:" + myen.getName()+",地址:" + myen.getAddr();
        return msg;
    }
    /**
     树的实体包含了No,Name,ParentNo,Idx 必须的属性(字段),它是树结构的描述.
     */
    public final void EnttiyTree() throws Exception {
        //创建父节点, 父节点的编号必须为1 ,父节点的ParentNo 必须是 0.
        FlowSort en = new FlowSort("1");
        en.setName("根目录");

        //创建子目录节点.
        FlowSort subEn = (FlowSort)en.DoCreateSubNode();
        subEn.setName("行政类");
        subEn.Update();

        //创建子目录的评级节点.
        FlowSort sameLevelSubEn = (FlowSort)subEn.DoCreateSameLevelNode();
        sameLevelSubEn.setName("业务类");
        sameLevelSubEn.Update();

        //创建子目录的下一级节点1.
        FlowSort sameLevelSubSubEn = (FlowSort)subEn.DoCreateSameLevelNode();
        sameLevelSubSubEn.setName("日常办公");
        sameLevelSubSubEn.Update();

        //创建子目录的下一级节点1.
        FlowSort sameLevelSubSubEn2 = (FlowSort)subEn.DoCreateSameLevelNode();
        sameLevelSubSubEn2.setName("人力资源");
        sameLevelSubSubEn2.Update();
        /**
         *   根目录
         *     行政类
         *        日常办公
         *        人力资源
         *     业务类
         *
         */
    }
    /**
     与文件相关的操作.
     */
    public final void EnttiyOptionWithFile() throws Exception {
        //把一个文件存入到entity.
        String fileFullName = "c:\\temp.doc";
        Flow fl = new Flow("001");
        fl.SaveFileToDB("ABC",fileFullName); //如果没有ABC 字段，系统就会自动创建。

        //把流存入数据库entity.
        byte[] betys = null;

        fl = new Flow("001");
        fl.SaveFileToDB("ABC", betys); //如果没有ABC 字段，系统就会自动创建。

        //获取文件.
        String saveTo = "c:\\tempfile.doc";
        fl.GetFileFromDB("ABC", saveTo);
        //特别说明，使用该方法，必须是已经存在数据库的一个实体，在没有插入之前，是不能调用的。
    }

}
