export const roles = [
  {
    label: "管理员",
    value: "admin",
  },
  {
    label: "用户",
    value: "user",
  },
];

export const menuList = [
  {
    roleFlag: "admin",
    name: "管理员",
    backMenu: [
      {
        name: "首页",
        path: "/home",
        icon: "HomeFilled",
        tableName: "",
        buttons: [],
      },
      {
        name: "个人信息",
        path: "/person",
        icon: "UserFilled",
        tableName: "person",
        buttons: [],
      },
      {
        name: "系统管理",
        path: "/system",
        icon: "Setting",
        children: [
          {
            name: "用户管理",
            path: "/user",
            tableName: "user",
            buttons: ["新增", "编辑", "删除"],
          },
          {
            name: "公告管理",
            path: "/notice",
            tableName: "notice",
            buttons: ["新增", "编辑", "删除"],
          },
          {
            name: "轮播图管理",
            path: "/banner",
            tableName: "banner",
            buttons: ["新增", "编辑", "删除"],
          },
        ],
      },
      {
        name: "景点相关",
        path: "/travel",
        icon: "Sunny",
        children: [
          {
            name: "景点分类管理",
            path: "/category",
            tableName: "category",
            buttons: ["新增", "编辑", "删除"],
          },
          {
            name: "景点管理",
            path: "/attraction",
            tableName: "attraction",
            buttons: ["新增", "编辑", "删除"],
          },
          {
            name: "旅游攻略管理",
            path: "/strategy",
            tableName: "strategy",
            buttons: ["新增", "编辑", "删除"],
          },
          {
            name: "攻略评论管理",
            path: "/comment",
            tableName: "comment",
            buttons: ["删除"],
          },
          {
            name: "收藏管理",
            path: "/collect",
            tableName: "collect",
            buttons: ["删除"],
          },
          {
            name: "点赞管理",
            path: "/like",
            tableName: "like",
            buttons: ["删除"],
          },
          {
            name: "门票管理",
            path: "/ticket",
            tableName: "ticket",
            buttons: ["新增", "编辑", "删除"],
          },
          {
            name: "门票订单管理",
            path: "/ticketOrder",
            tableName: "ticketOrder",
            buttons: ["删除"],
          },
        ],
      },
    ],
  },
];
