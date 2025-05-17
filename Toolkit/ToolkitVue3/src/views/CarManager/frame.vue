
<!-- - 嵌入方式的表单演示<div class=""></div>
<table>
    <tr> </td>
</table> -->

<!-- <div>
- 演示内容.
1. 本文件演示从一个实体上发起流程，并把当前实体的主表的重要字段传入，流程的开始节点表单.
2. 启动的流程
- 用车申请: 
1. 用车申请: 传入基础信息即可.  071      FlowNo=097

- 车辆维修:  072
1. 启动维修流程时，如何把车辆从表，附件信息传入到开始节点表单.
</div> -->
<template>
    <div class="allHeight">
      <Spin :spinning="loading">
        <div>
          <div class="controls"> 
            <ul>
              <div>-演示内容.</div>
              <li>1、本文件演示一个嵌入式流程页面,您可以定义一个页面，绑定到该节点上.</li>
              <li>2、启动的流程</li>
              <div>- 嵌入式流程示例:</div>
              <li>1、流程启动: 嵌入一个自定义页面,该页面有一个Save()的function.  FlowNo=083</li>
              <li><div>2、启动方式:</div>
                <ol> 
                  <li>当用户点击框架外面的工具栏上的【保存】按钮或者【发送】按钮，就会触发这个函数.</li>
                  <li>您需要在Save()的function里完成数据完整性效验与数据保存.</li>
                  <li>如果保存成功就return true, 保存失败就return false.</li>
                </ol>
              </li>
            </ul>
          </div>
        </div>
        <div class="table_cont">
          <Table :columns="column" :data-source="dataSource">
            <template #headerCell="{ column, record }">
              <template v-if="column.dataIndex == 'action'">
                <div style="text-align: center;">
                  操作
                </div>
              </template>
            </template>
            <template #bodyCell="{ column, record }">
              <template v-if="column.dataIndex == 'action'">
                <div  style="display: flex;width: 100%;justify-content: space-evenly;">
                  <Button type="primary" size="small" @click="doStart(record.FlowUrl)">启动流程</Button>
                </div>
              </template>
            </template>
          </Table>
        </div>
      </Spin>
    </div>
  </template>
  <script lang="ts" setup>
  import { useUserStore } from '@/stores/user';
  import { Table, Spin, Button } from 'ant-design-vue';
  import type { ColumnType } from 'ant-design-vue/lib/table';
  import { ref } from 'vue';

  const loading = ref(false);

  const userStore = useUserStore();
  const host = import.meta.env.VITE_GLOB_VUE3_URL   //获取vue3Host
  const Token = userStore.getToken;

  const column: ColumnType[] = [
    {
      title: '流程编号',
      dataIndex: 'FlowNo',
      fixed: 'left',
      width: 100,
      ellipsis: true,
    },
    {
      title: '流程名称',
      dataIndex: 'FlowName',
      fixed: 'left',
      width: 100,
      ellipsis: true,
    },
    {
      title: '流程路径',
      dataIndex: 'FlowUrl',
      fixed: 'left',
      width: 250,
      ellipsis: true,
    },
    {
      title: '操作',
      dataIndex: 'action',
      fixed: 'right',
      width: 80,
    }
  ];

  const dataSource = [
        {
            FlowNo:'083',
            FlowName:'嵌入式演示',
            FlowUrl:`${host}/#/WF/Port?DoWhat=StartFlow&FlowNo=083&token=${Token}`,
        },
     ];
  /**
   * 启动:跳转页面
   * @param res 链接地址
   */
  const doStart = async (res:string) =>{
    window.open(res);
  }
  </script>
  <style lang="less" scoped>
  .ant-spin-nested-loading {
    max-height: 100%;
  
    .ant-spin-container {
      max-height: 100%;
    }
  }
  
  .allHeight {
    padding-top: 15px;
    height: 100%;
    background-color: #eeeeee;
  }
  
  
  .total {
    height: 40px;
    margin-top: 20px;
  }
  
  li{
    padding-top: 10px;
  }
  .controls{
    height: 55%;
    padding: 15px;
    margin: 0 15px;
    box-shadow: 1px 0px 5px 5px #eae4e4;
    background-color: #fff;
      .car_btn{
          margin-left: 10%;
        }
  }
  .table_cont{
    padding: 15px;
  }
  .btn_Back {
    float: right;
  }

  </style>

