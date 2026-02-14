import { createRouter, createWebHistory } from "vue-router";
import { ElMessage } from "element-plus";
import useUserStore from "@/stores/userStore";

const routes = [
  {
    path: "/",
    name: "Layout",
    redirect: "/home",
    component: () => import("../layouts/index.vue"),
    meta: {
      title: "入口",
    },
    children: [
      {
        path: "/home",
        name: "Home",
        component: () => import("@/views/system/home/home.vue"),
        meta: {
          title: "首页",
        },
      },
      {
        path: "/user",
        name: "User",
        component: () => import("@/views/system/user.vue"),
        meta: {
          title: "用户管理",
        },
      },
      {
        path: "/notice",
        name: "Notice",
        component: () => import("@/views/system/notice.vue"),
        meta: {
          title: "公告管理",
        },
      },
      {
        path: "/banner",
        name: "Banner",
        component: () => import("@/views/system/banner.vue"),
        meta: {
          title: "轮播图管理",
        },
      },
      {
        path: "/category",
        name: "Category",
        component: () => import("@/views/system/category.vue"),
        meta: {
          title: "景点分类管理",
        },
      },
      {
        path: "/attraction",
        name: "Attraction",
        component: () => import("@/views/system/attraction.vue"),
        meta: {
          title: "景点管理",
        },
      },
      {
        path: "/strategy",
        name: "Strategy",
        component: () => import("@/views/system/strategy.vue"),
        meta: {
          title: "旅游攻略管理",
        },
      },
      {
        path: "/comment",
        name: "Comment",
        component: () => import("@/views/system/comment.vue"),
        meta: {
          title: "景点评论管理",
        },
      },
      {
        path: "/collect",
        name: "Collect",
        component: () => import("@/views/system/collect.vue"),
        meta: {
          title: "收藏管理",
        },
      },
      {
        path: "/like",
        name: "Like",
        component: () => import("@/views/system/like.vue"),
        meta: {
          title: "点赞管理",
        },
      },
      {
        path: "/ticket",
        name: "Ticket",
        component: () => import("@/views/system/ticket.vue"),
        meta: {
          title: "门票管理",
        },
      },
      {
        path: "/ticketOrder",
        name: "TicketOrder",
        component: () => import("@/views/system/ticketOrder.vue"),
        meta: {
          title: "门票订单管理",
        },
      },
      {
        path: "/person",
        name: "Person",
        component: () => import("@/views/common/person.vue"),
        meta: {
          title: "个人信息",
        },
      },
    ],
  },
  {
    path: "/front",
    name: "FrontLayout",
    redirect: "/front/home",
    component: () => import("../front-layouts/index.vue"),
    meta: {
      title: "前台首页",
    },
    children: [
      {
        path: "/front/home",
        name: "FrontHome",
        component: () => import("../views/front/home.vue"),
        meta: {
          title: "前台首页",
        },
      },
      {
        path: "/front/attractions",
        name: "FrontAttractions",
        component: () => import("@/views/front/attractionList.vue"),
        meta: {
          title: "景点列表",
        },
      },
      {
        path: "/front/strategies",
        name: "FrontStrategies",
        component: () => import("@/views/front/strategyList.vue"),
        meta: {
          title: "攻略列表",
        },
      },
      {
        path: "/front/tickets",
        name: "FrontTickets",
        component: () => import("@/views/front/ticketList.vue"),
        meta: {
          title: "门票列表",
        },
      },
      {
        path: "/front/ai-chat",
        name: "FrontAiChat",
        component: () => import("@/views/front/aiChat.vue"),
        meta: {
          title: "AI助手",
          hideFooter: true,
        },
      },
      {
        path: "/front/attraction/:id",
        name: "FrontAttractionDetail",
        component: () => import("@/views/front/attractionDetail.vue"),
        meta: {
          title: "景点详情",
        },
      },
      {
        path: "/front/strategy/:id",
        name: "FrontStrategyDetail",
        component: () => import("@/views/front/strategyDetail.vue"),
        meta: {
          title: "攻略详情",
        },
      },
      {
        path: "/front/ticket/:id",
        name: "FrontTicketDetail",
        component: () => import("@/views/front/ticketDetail.vue"),
        meta: {
          title: "门票详情",
        },
      },
      {
        path: "/front/myTicketOrders",
        name: "FrontMyTicketOrders",
        component: () => import("@/views/front/myTicketOrder.vue"),
        meta: {
          title: "我的订单",
        },
      },
      {
        path: "/front/myCollections",
        name: "FrontMyCollections",
        component: () => import("@/views/front/myCollections.vue"),
        meta: {
          title: "我的收藏",
        },
      },
      {
        path: "/front/myLikes",
        name: "FrontMyLikes",
        component: () => import("@/views/front/myLikes.vue"),
        meta: {
          title: "我的点赞",
        },
      },
      {
        path: "/front/myStrategy",
        name: "MyStrategy",
        component: () => import("@/views/front/myStrategy.vue"),
        meta: {
          title: "我的攻略",
        },
      },
      {
        path: "/front/publishStrategy",
        name: "PublishStrategy",
        component: () => import("@/views/front/publishStrategy.vue"),
        meta: {
          title: "发布攻略",
        },
      },
      {
        path: "/front/person",
        name: "FrontPerson",
        component: () => import("@/views/front/person.vue"),
        meta: {
          title: "个人信息",
        },
      },
    ],
  },
  {
    path: "/login",
    name: "Login",
    component: () => import("../views/common/login.vue"),
    meta: {
      title: "登录",
    },
  },
  {
    path: "/register",
    name: "Register",
    component: () => import("../views/common/register.vue"),
    meta: {
      title: "注册",
    },
  },
  {
    path: "/:pathMatch(.*)*",
    name: "NotFound",
    component: () => import("../views/common/notFound.vue"),
    meta: {
      title: "404",
    },
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

// 全局前置守卫：前台/后台独立校验
router.beforeEach((to, from, next) => {
  document.title = to.meta.title || "Vue App";

  const userStore = useUserStore();
  const isFront = to.path.startsWith("/front");
  const whiteList = ["/login", "/register", "/:pathMatch(.*)*"];

  if (whiteList.includes(to.path)) {
    return next();
  }

  if (isFront) {
    if (!userStore.frontToken) {
      ElMessage.error("请先登录前台账户");
      return next("/login");
    }
    return next();
  } else {
    if (!userStore.adminToken) {
      ElMessage.error("请先登录后台管理员");
      return next("/login");
    }
    if (userStore.adminRoleFlag !== "admin") {
      ElMessage.error("无权限访问后台");
      return next("/login");
    }
    return next();
  }
});

export default router;
