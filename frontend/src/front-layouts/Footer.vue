<script setup lang="ts">
import config from "../config";
</script>

<template>
  <footer class="front-footer">
    <div class="footer-container">
      <div class="footer-content">
        <!-- 关于我们 -->
        <div class="footer-section">
          <div class="logo-section">
            <img src="../assets/logo.png" alt="Logo" class="footer-logo" />
            <h4 class="system-name">{{ config.frontName }}</h4>
          </div>
          <p class="description">
            {{ config.frontDesc }}
          </p>
        </div>
      </div>
      
      <!-- 底部版权信息 -->
      <div class="footer-bottom">
        <p class="copyright">
          © 2024 {{ config.frontName }}. All rights reserved.
        </p>
      </div>
    </div>
  </footer>
</template>

<style lang="scss" scoped>
.front-footer {
  background: #f8f9fa;
  border-top: 1px solid #e8e8e8;
  padding: 60px 0 30px;
  margin-top: auto;

  .footer-container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 20px;
  }

  .footer-content {
    text-align: center;
    margin-bottom: 40px;

    .footer-section {
      .logo-section {
        display: flex;
        align-items: center;
        justify-content: center;
        margin-bottom: 20px;

        .footer-logo {
          width: 48px;
          height: 48px;
          margin-right: 12px;
        }

        .system-name {
          font-size: 24px;
          font-weight: 700;
          color: var(--primary-color);
          margin: 0;
        }
      }

      .description {
        color: #666;
        font-size: 16px;
        line-height: 1.6;
        max-width: 600px;
        margin: 0 auto;
      }
    }
  }

  .footer-bottom {
    border-top: 1px solid #e8e8e8;
    padding-top: 30px;
    text-align: center;

    .copyright {
      color: #999;
      font-size: 14px;
      margin: 0;
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .front-footer {
    padding: 40px 0 20px;
    
    .footer-container {
      padding: 0 15px;
    }

    .footer-content {
      .footer-section {
        .logo-section {
          .footer-logo {
            width: 40px;
            height: 40px;
          }

          .system-name {
            font-size: 20px;
          }
        }

        .description {
          font-size: 14px;
        }
      }
    }
  }
}

@media (max-width: 480px) {
  .front-footer {
    padding: 30px 0 15px;

    .footer-content {
      margin-bottom: 30px;
      
      .footer-section {
        .logo-section {
          flex-direction: column;
          
          .footer-logo {
            width: 36px;
            height: 36px;
            margin-right: 0;
            margin-bottom: 8px;
          }

          .system-name {
            font-size: 18px;
          }
        }

        .description {
          font-size: 13px;
        }
      }
    }

    .footer-bottom {
      padding-top: 20px;
      
      .copyright {
        font-size: 12px;
      }
    }
  }
}
</style>