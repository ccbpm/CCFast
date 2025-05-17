<template>
    <div class="allHeight">
      <Spin :spinning="loading" class="allHeight">
        <Table :columns="column" :data-source="dataSource" :defaultExpandAllRows="true">
          <template #headerCell="{ column, record }">
            <template v-if="column.dataIndex == 'action'">
              <div style="text-align: center;">
                操作
              </div>
            </template>
          </template>
          <template #bodyCell="{ column, record }">
            <template v-if="column.dataIndex == 'action'">
              <div v-if="record.Paras" style="display: flex;width: 100%;justify-content: space-evenly;">
                <Button type="primary" size="small" @click="doCopy(record.Paras)">复制</Button>
                <Button type="primary" size="small" @click="doStart(record.Paras)">启动</Button>
              </div>
            </template>
          </template>
        </Table>
      </Spin>
    </div>
  </template>
  <script lang="ts" setup>
  import { useUserStore } from '@/stores/user';
  import { Table, Spin, Button, message } from 'ant-design-vue';
  import type { ColumnType } from 'ant-design-vue/lib/table';
  import { ref } from 'vue';
  import  menuList  from '@/portal/menu';

  const loading = ref(false);
  const dataSource = ref<any>([]);
  const userStore = useUserStore();
  const host = import.meta.env.VITE_GLOB_VUE3_URL   //获取vue3Host
  const Token = userStore.getToken;
  const column: ColumnType[] = [
    {
      title: '功能',
      dataIndex: 'Name',
      key: 'Name',
      fixed: 'left',
      width: 250,
      ellipsis: true,
    }, {
      title: '路径',
      dataIndex: 'Paras',
      key: 'Paras',
      fixed: 'left',
      customRender: ({ value }) => {
        if(value){
          const str =  `${host}/#/WF/Port?${value}&token=${Token}`
          return str;
        }
      },
    },{
      title: '操作',
      dataIndex: 'action',
      key: 'action',
      fixed: 'right',
      width: 200,
    }
  ];
  const InitPage = () => {
    dataSource.value = menuList;
  };
  /**
   * 复制
   * @param res 复制数据
   */
  const doCopy = async (res:string) =>{
    const copyData = await changeRowData(res);
    await navigator.clipboard.writeText(copyData);
    message.success('复制成功');
  }
  /**
   * 启动
   * @param res 启动数据
   */
  const doStart = async (res:string) =>{
    const copyData = await changeRowData(res);
    window.open(copyData);
  }

  /**
   * 处理行数据
   * @param str 行数据处理
   */
  const changeRowData =(str: any)=>{
    const rowStr =  `${host}/#/WF/Port?${str}&token=${Token}`
    return rowStr;
  }

  InitPage();
  
  </script>
  <style lang="less" scoped>
  .ant-spin-nested-loading {
    max-height: 100%;
  
    .ant-spin-container {
      max-height: 100%;
    }
  }
  
  .allHeight {
    height: 100%;
  }
  
  .total {
    height: 40px;
    margin-top: 20px;
  }
  
  .btn_Back {
    float: right;
  }
  </style>