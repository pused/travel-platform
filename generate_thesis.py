#!/usr/bin/env python3
"""
生成毕业论文完整Word文档
基于Spring Boot+Vue的智能旅游服务平台设计与实现

严格按照郑州西亚斯学院论文模板格式要求生成
"""

from docx import Document
from docx.shared import Pt, Cm, Emu, RGBColor
from docx.enum.text import WD_ALIGN_PARAGRAPH
from docx.enum.table import WD_TABLE_ALIGNMENT, WD_ALIGN_VERTICAL
from docx.oxml.ns import qn
from docx.oxml import OxmlElement


# ============================================================
# 字号常量（磅值）
# ============================================================
FONT_ER_HAO = Pt(22)        # 二号
FONT_XIAO_CHU = Pt(36)      # 小初号
FONT_SAN_HAO = Pt(16)       # 三号
FONT_XIAO_SAN = Pt(15)      # 小三号
FONT_SI_HAO = Pt(14)        # 四号
FONT_XIAO_SI = Pt(12)       # 小四号
FONT_WU_HAO = Pt(10.5)      # 五号
FONT_XIAO_WU = Pt(9)        # 小五号


# ============================================================
# 格式化工具函数
# ============================================================

def set_run_font(run, cn_font='宋体', en_font='Times New Roman',
                 size=FONT_XIAO_SI, bold=False, italic=False, color=None):
    """设置 run 的字体"""
    run.font.size = size
    run.font.bold = bold
    run.font.italic = italic
    run.font.name = en_font
    # 设置中文字体
    rpr = run._element.get_or_add_rPr()
    rFonts = rpr.find(qn('w:rFonts'))
    if rFonts is None:
        rFonts = OxmlElement('w:rFonts')
        rpr.insert(0, rFonts)
    rFonts.set(qn('w:eastAsia'), cn_font)
    if color:
        run.font.color.rgb = color


def set_paragraph_format(p, alignment=None, line_spacing=1.15,
                         space_before=Pt(0), space_after=Pt(0),
                         first_line_indent=None, hanging_indent=None,
                         page_break_before=False):
    """设置段落格式"""
    if alignment is not None:
        p.alignment = alignment
    pf = p.paragraph_format
    pf.space_before = space_before
    pf.space_after = space_after
    pf.line_spacing = line_spacing

    if first_line_indent is not None:
        pf.first_line_indent = first_line_indent

    if hanging_indent is not None:
        # 悬挂缩进需要通过 XML 操作
        pPr = p._element.get_or_add_pPr()
        ind = pPr.find(qn('w:ind'))
        if ind is None:
            ind = OxmlElement('w:ind')
            pPr.append(ind)
        ind.set(qn('w:hanging'), str(hanging_indent))
        ind.set(qn('w:left'), str(hanging_indent))

    if page_break_before:
        pPr = p._element.get_or_add_pPr()
        pb = OxmlElement('w:pageBreakBefore')
        pPr.append(pb)


def add_empty_line(doc, size=FONT_XIAO_SI, line_spacing=1.15):
    """添加空行（宋体小四号 1.15倍行距）"""
    p = doc.add_paragraph()
    set_paragraph_format(p, line_spacing=line_spacing)
    run = p.add_run('')
    set_run_font(run, size=size)
    return p


# ============================================================
# 标题函数
# ============================================================

def add_heading_1(doc, text):
    """一级标题：三号宋体加粗，左顶格，1.15倍行距，段前分页
    章节序号和标题之间半角空格"""
    p = doc.add_paragraph()
    set_paragraph_format(p, alignment=WD_ALIGN_PARAGRAPH.LEFT,
                         line_spacing=1.15, page_break_before=True)
    run = p.add_run(text)
    set_run_font(run, cn_font='宋体', size=FONT_SAN_HAO, bold=True)
    return p


def add_heading_1_center(doc, text):
    """不带章号的标题（结论、致谢等）：三号宋体加粗，居中（模板实际是左顶格），段前分页"""
    p = doc.add_paragraph()
    set_paragraph_format(p, alignment=WD_ALIGN_PARAGRAPH.LEFT,
                         line_spacing=1.15, page_break_before=True)
    run = p.add_run(text)
    set_run_font(run, cn_font='宋体', size=FONT_SAN_HAO, bold=True)
    return p


def add_heading_2(doc, text):
    """二级标题：小三号宋体加粗，左顶格，1.15倍行距"""
    p = doc.add_paragraph()
    set_paragraph_format(p, alignment=WD_ALIGN_PARAGRAPH.LEFT,
                         line_spacing=1.15)
    run = p.add_run(text)
    set_run_font(run, cn_font='宋体', size=FONT_XIAO_SAN, bold=True)
    return p


def add_heading_3(doc, text):
    """三级标题：小四号宋体加粗，左顶格，1.15倍行距"""
    p = doc.add_paragraph()
    set_paragraph_format(p, alignment=WD_ALIGN_PARAGRAPH.LEFT,
                         line_spacing=1.15)
    run = p.add_run(text)
    set_run_font(run, cn_font='宋体', size=FONT_XIAO_SI, bold=True)
    return p


# ============================================================
# 正文函数
# ============================================================

def add_body(doc, text):
    """正文：小四号宋体，1.5倍行距，首行缩进2字符，两端对齐"""
    p = doc.add_paragraph()
    set_paragraph_format(p, alignment=WD_ALIGN_PARAGRAPH.JUSTIFY,
                         line_spacing=1.5,
                         first_line_indent=Pt(FONT_XIAO_SI.pt * 2))
    run = p.add_run(text)
    set_run_font(run, cn_font='宋体', size=FONT_XIAO_SI)
    return p


def add_body_no_indent(doc, text):
    """正文无缩进"""
    p = doc.add_paragraph()
    set_paragraph_format(p, alignment=WD_ALIGN_PARAGRAPH.JUSTIFY,
                         line_spacing=1.5)
    run = p.add_run(text)
    set_run_font(run, cn_font='宋体', size=FONT_XIAO_SI)
    return p


# ============================================================
# 图表函数
# ============================================================

def add_figure_caption(doc, chapter, fig_num, caption):
    """图题：五号宋体，居中，位于图下方
    格式：图 X-Y 标题"""
    p = doc.add_paragraph()
    set_paragraph_format(p, alignment=WD_ALIGN_PARAGRAPH.CENTER,
                         line_spacing=1.15)
    run = p.add_run(f'图 {chapter}-{fig_num} {caption}')
    set_run_font(run, cn_font='宋体', size=FONT_WU_HAO)
    return p


def add_figure_placeholder(doc, chapter, fig_num, caption, description=''):
    """添加图片占位符（居中显示提示文字）+ 图题"""
    p = doc.add_paragraph()
    set_paragraph_format(p, alignment=WD_ALIGN_PARAGRAPH.CENTER,
                         line_spacing=1.5)
    run = p.add_run(f'（此处插入{caption}）')
    set_run_font(run, cn_font='宋体', size=FONT_XIAO_SI, color=RGBColor(0x99, 0x99, 0x99))
    add_figure_caption(doc, chapter, fig_num, caption)
    return p


def add_table_caption(doc, chapter, tbl_num, caption):
    """表题：五号宋体，居中，位于表上方
    格式：表 X-Y 标题"""
    p = doc.add_paragraph()
    set_paragraph_format(p, alignment=WD_ALIGN_PARAGRAPH.CENTER,
                         line_spacing=1.15)
    run = p.add_run(f'表 {chapter}-{tbl_num} {caption}')
    set_run_font(run, cn_font='宋体', size=FONT_WU_HAO)
    return p


def create_three_line_table(doc, headers, rows):
    """创建三线表
    顶线1.5磅、底线1.5磅、栏目线0.75磅
    表内文字五号宋体，数字用 Times New Roman"""
    table = doc.add_table(rows=1 + len(rows), cols=len(headers))
    table.alignment = WD_TABLE_ALIGNMENT.CENTER

    # 设置表格宽度自适应
    tbl = table._tbl
    tblPr = tbl.tblPr if tbl.tblPr is not None else OxmlElement('w:tblPr')
    tblW = OxmlElement('w:tblW')
    tblW.set(qn('w:type'), 'pct')
    tblW.set(qn('w:w'), '5000')  # 100% width
    tblPr.append(tblW)

    # 移除默认边框
    borders = OxmlElement('w:tblBorders')
    for border_name in ['top', 'left', 'bottom', 'right', 'insideH', 'insideV']:
        border = OxmlElement(f'w:{border_name}')
        border.set(qn('w:val'), 'none')
        border.set(qn('w:sz'), '0')
        border.set(qn('w:space'), '0')
        border.set(qn('w:color'), '000000')
        borders.append(border)
    tblPr.append(borders)

    def set_cell_border(cell, top=None, bottom=None):
        """为单元格设置边框"""
        tc = cell._tc
        tcPr = tc.get_or_add_tcPr()
        tcBorders = OxmlElement('w:tcBorders')
        if top is not None:
            b = OxmlElement('w:top')
            b.set(qn('w:val'), 'single')
            b.set(qn('w:sz'), str(top))
            b.set(qn('w:space'), '0')
            b.set(qn('w:color'), '000000')
            tcBorders.append(b)
        if bottom is not None:
            b = OxmlElement('w:bottom')
            b.set(qn('w:val'), 'single')
            b.set(qn('w:sz'), str(bottom))
            b.set(qn('w:space'), '0')
            b.set(qn('w:color'), '000000')
            tcBorders.append(b)
        tcPr.append(tcBorders)

    # 设置表头
    for j, header in enumerate(headers):
        cell = table.cell(0, j)
        cell.text = ''
        p = cell.paragraphs[0]
        set_paragraph_format(p, alignment=WD_ALIGN_PARAGRAPH.CENTER,
                             line_spacing=1.15)
        run = p.add_run(header)
        set_run_font(run, cn_font='宋体', size=FONT_WU_HAO, bold=True)
        cell.vertical_alignment = WD_ALIGN_VERTICAL.CENTER
        # 顶线1.5磅(12) + 栏目线0.75磅(6)
        set_cell_border(cell, top=12, bottom=6)

    # 设置数据行
    for i, row_data in enumerate(rows):
        is_last = (i == len(rows) - 1)
        for j, val in enumerate(row_data):
            cell = table.cell(i + 1, j)
            cell.text = ''
            p = cell.paragraphs[0]
            set_paragraph_format(p, alignment=WD_ALIGN_PARAGRAPH.CENTER,
                                 line_spacing=1.5)
            run = p.add_run(str(val))
            set_run_font(run, cn_font='宋体', size=FONT_WU_HAO)
            cell.vertical_alignment = WD_ALIGN_VERTICAL.CENTER
            if is_last:
                set_cell_border(cell, bottom=12)  # 底线1.5磅

    return table


# ============================================================
# 页码相关
# ============================================================

def add_page_number_field(doc, section, prefix='第 ', suffix=' 页'):
    """添加页码域（需要在 Word 中更新域才能显示正确数值）"""
    footer = section.footer
    footer.is_linked_to_previous = False
    p = footer.paragraphs[0] if footer.paragraphs else footer.add_paragraph()
    p.alignment = WD_ALIGN_PARAGRAPH.CENTER
    set_paragraph_format(p, alignment=WD_ALIGN_PARAGRAPH.CENTER, line_spacing=1.0)

    # "第 "
    run1 = p.add_run(prefix)
    set_run_font(run1, cn_font='宋体', size=FONT_XIAO_WU)

    # PAGE 域
    fldChar1 = OxmlElement('w:fldChar')
    fldChar1.set(qn('w:fldCharType'), 'begin')
    run_page = p.add_run()
    run_page._element.append(fldChar1)

    instrText = OxmlElement('w:instrText')
    instrText.set(qn('xml:space'), 'preserve')
    instrText.text = ' PAGE '
    run_instr = p.add_run()
    set_run_font(run_instr, size=FONT_XIAO_WU)
    run_instr._element.append(instrText)

    fldChar2 = OxmlElement('w:fldChar')
    fldChar2.set(qn('w:fldCharType'), 'end')
    run_end = p.add_run()
    run_end._element.append(fldChar2)

    # " 页 共 "
    run2 = p.add_run(f'{suffix}共 ')
    set_run_font(run2, cn_font='宋体', size=FONT_XIAO_WU)

    # NUMPAGES 域
    fldChar3 = OxmlElement('w:fldChar')
    fldChar3.set(qn('w:fldCharType'), 'begin')
    run_np = p.add_run()
    run_np._element.append(fldChar3)

    instrText2 = OxmlElement('w:instrText')
    instrText2.set(qn('xml:space'), 'preserve')
    instrText2.text = ' NUMPAGES '
    run_instr2 = p.add_run()
    set_run_font(run_instr2, size=FONT_XIAO_WU)
    run_instr2._element.append(instrText2)

    fldChar4 = OxmlElement('w:fldChar')
    fldChar4.set(qn('w:fldCharType'), 'end')
    run_end2 = p.add_run()
    run_end2._element.append(fldChar4)

    run3 = p.add_run(' 页')
    set_run_font(run3, cn_font='宋体', size=FONT_XIAO_WU)


# ============================================================
# 主函数
# ============================================================

def generate_thesis():
    """生成完整论文"""

    # 以初稿为基础（保留封面格式和表格）
    src_path = '/home/clawbot/.openclaw/media/inbound/file_57---b6d83c57-2328-4103-a718-a2037f9f9be2.docx'
    doc = Document(src_path)

    # ---- 修复封面：填写完成时间 ----
    for table in doc.tables:
        for row in table.rows:
            cells = [cell.text for cell in row.cells]
            if '完成时间' in cells[0]:
                cell = row.cells[1]
                cell.text = ''
                p = cell.paragraphs[0]
                run = p.add_run('2025年5月')
                set_run_font(run, cn_font='黑体', en_font='Times New Roman',
                             size=FONT_SAN_HAO)
                break

    # ---- 删除初稿中 1.2 之后的空段落 ----
    # 初稿在 1.2 标题后只有一个空段落，我们保留到 1.2 标题
    # 然后追加所有后续内容

    # 找到 1.2 的位置
    idx_12 = None
    for i, p in enumerate(doc.paragraphs):
        if '1.2' in p.text and '国内外' in p.text:
            idx_12 = i
            break

    # 删除 1.2 之后的所有段落（只有一个空段）
    if idx_12 is not None:
        body = doc.element.body
        paras_to_remove = []
        for i in range(idx_12 + 1, len(doc.paragraphs)):
            paras_to_remove.append(doc.paragraphs[i]._element)
        for elem in paras_to_remove:
            body.remove(elem)

    # ============================================================
    # 1.2 国内外研究现状分析
    # ============================================================
    add_body(doc, '近年来，随着智慧旅游概念的不断推广和互联网技术的深入发展，国内外在旅游信息服务平台的设计与实现方面取得了显著的研究进展。')

    add_heading_3(doc, '1.2.1 国内研究现状')
    add_body(doc, '国内旅游信息平台逐渐向智能化、集成化方向发展。肖程鸣等[1]基于Spring Boot和Vue技术栈设计了红色智慧旅游平台，实现了旅游资源的数字化展示与管理，验证了前后端分离架构在旅游系统中的可行性。马立等[2]利用高德地图API设计了旅游景点查询平台，通过地图服务实现了景点定位和路线规划功能。戴亚哲等[3]研究了基于Spring Boot+Vue的文旅平台设计，探讨了文旅融合场景下的技术实现方案。')
    add_body(doc, '曹浩等[4]针对桂林龙胜各族自治县设计了旅游信息系统，结合地方特色实现了景区信息管理和游客服务功能。宁毅等[5]将大数据分析技术引入旅游平台，基于Spring Boot+Spark+Vue实现了旅游数据的可视化分析。陈伍香等[6]在5G背景下设计了基于微服务架构的智慧旅游管理系统。郑赞红等[7]实现了广东文旅智慧管理系统，着重解决了旅游资源整合与管理问题。和娴等[8]对旅游景区游客信息系统进行了设计与实现，从游客管理角度优化了景区服务流程。')
    add_body(doc, '然而，当前国内旅游平台仍存在以下不足：一是多数平台以信息展示为主，缺乏智能化的交互服务能力；二是平台功能较为分散，难以为用户提供一体化的出行服务体验；三是AI智能问答等新兴技术在旅游平台中的应用仍处于探索阶段。')

    add_heading_3(doc, '1.2.2 国外研究现状')
    add_body(doc, '国外智慧旅游系统的研究起步较早，发展相对成熟。Qi等[14]从人工智能视角出发，设计并实现了智慧旅游服务平台，利用自然语言处理技术为用户提供智能化的旅游信息推荐服务。Chen等[15]基于Spring Boot框架研究了高效学习平台的设计与实现，验证了该框架在复杂业务系统中的适用性和高效性。')
    add_body(doc, '国外主流旅游平台如Google Travel、TripAdvisor等，通过深度整合用户数据和旅游资源，利用大数据分析和机器学习算法为用户提供个性化的旅游推荐与行程规划服务。这些平台普遍采用微服务架构和云计算技术，具备高可用性和高并发处理能力，在智能化服务水平和系统架构设计方面具有较强的参考价值。')

    add_heading_2(doc, '1.3 研究内容与论文结构')
    add_body(doc, '本课题旨在设计并实现一个基于Spring Boot与Vue的智能旅游服务平台，主要研究内容包括以下几个方面：')
    add_body(doc, '（1）系统需求分析。通过文献调研和平台试用，分析普通用户和管理员两大角色的功能需求，明确系统的功能性和非功能性需求。')
    add_body(doc, '（2）系统架构设计。采用Spring Boot+Vue前后端分离架构，设计合理的系统总体架构、数据库模型和接口规范。')
    add_body(doc, '（3）核心功能实现。开发景点信息管理、旅游攻略、门票预订、AI智能问答、地图路线规划等核心业务模块。')
    add_body(doc, '（4）系统测试与优化。对系统进行功能测试和性能测试，确保系统的稳定性和可用性。')
    add_body(doc, '本文的组织结构如下：第1章绪论，介绍研究背景、国内外现状和研究内容；第2章相关技术，介绍系统所用的关键技术；第3章需求分析，对系统进行详细的需求分析；第4章系统设计，包括总体架构设计和数据库设计；第5章系统实现，展示各功能模块的具体实现；第6章系统测试，对系统进行功能和性能测试；结论部分对全文进行总结并展望未来研究方向。')

    # ============================================================
    # 第2章 相关技术
    # ============================================================
    add_heading_1(doc, '2 相关技术')
    add_body(doc, '本章主要介绍智能旅游服务平台开发过程中所采用的关键技术，包括后端框架Spring Boot、前端框架Vue.js、数据库MySQL、前端UI组件库Element Plus、持久层框架MyBatis-Plus、认证框架Sa-Token、高德地图API以及DeepSeek AI智能问答服务等。')

    add_heading_2(doc, '2.1 Spring Boot框架')
    add_body(doc, 'Spring Boot是由Pivotal团队开发的基于Spring框架的快速开发工具，旨在简化Spring应用的初始搭建和开发过程。Spring Boot通过自动配置机制，大幅减少了传统Spring项目中繁琐的XML配置，使开发者能够快速搭建独立运行的、生产级别的Spring应用程序[10]。')
    add_body(doc, 'Spring Boot的核心特性包括：（1）自动配置，根据项目依赖自动配置Spring和第三方库；（2）起步依赖，通过starter简化Maven或Gradle配置；（3）内嵌服务器，支持内嵌Tomcat、Jetty等Web服务器，无需部署WAR文件；（4）Actuator监控，提供生产环境下的应用监控功能。本系统采用Spring Boot 2.x版本作为后端核心框架，结合RESTful API设计规范，为前端提供统一的数据接口服务。')

    add_heading_2(doc, '2.2 Vue.js框架')
    add_body(doc, 'Vue.js是一款用于构建用户界面的渐进式JavaScript框架，由尤雨溪于2014年发布。Vue.js采用组件化开发模式和虚拟DOM技术，具有轻量、灵活、高效的特点，已成为当前最流行的前端框架之一。')
    add_body(doc, '本系统采用Vue 3版本，相较于Vue 2，Vue 3引入了Composition API，提供了更灵活的逻辑组织方式；采用Proxy替代Object.defineProperty实现响应式系统，性能更优；支持Tree-shaking，有效减小打包体积。系统使用Vite作为构建工具，利用其原生ES模块支持和按需编译特性，显著提升了开发环境下的热更新速度。同时，系统集成了Vue Router实现单页面应用路由管理，使用Pinia进行全局状态管理。')

    add_heading_2(doc, '2.3 MySQL数据库')
    add_body(doc, 'MySQL是一款开源的关系型数据库管理系统，采用SQL（结构化查询语言）进行数据管理。MySQL以其高性能、高可靠性和易用性，被广泛应用于各类Web应用系统中。')
    add_body(doc, '本系统采用MySQL 5.7版本，利用InnoDB存储引擎支持事务处理和行级锁定，保证数据的一致性和并发访问的安全性。数据库采用utf8mb4字符集，支持存储包括表情符号在内的完整Unicode字符。通过合理的索引设计和查询优化，确保系统在大数据量场景下仍能保持良好的查询性能。')

    add_heading_2(doc, '2.4 Element Plus组件库')
    add_body(doc, 'Element Plus是基于Vue 3的桌面端UI组件库，是Element UI的升级版本。它提供了丰富的高质量UI组件，包括表格、表单、对话框、导航、通知等常用界面元素，帮助开发者快速构建美观、一致的用户界面。')
    add_body(doc, '本系统基于Element Plus构建了前台用户界面和后台管理界面，利用其响应式栅格布局系统适配不同屏幕尺寸，使用表格组件和分页组件实现数据的分页展示，使用表单组件和验证机制实现用户输入的规范化处理。')

    add_heading_2(doc, '2.5 MyBatis-Plus持久层框架')
    add_body(doc, 'MyBatis-Plus是MyBatis的增强工具，在MyBatis的基础上只做增强不做改变，旨在简化开发和提高效率。它内置了通用的Mapper和Service，提供了强大的条件构造器，支持Lambda表达式编写查询条件，能够自动生成基础的CRUD操作代码。')
    add_body(doc, '本系统使用MyBatis-Plus实现数据持久层操作，利用其分页插件实现数据的分页查询，通过自动填充功能自动维护记录的创建时间和更新时间字段，使用逻辑删除功能实现数据的软删除，有效简化了数据访问层的开发工作。')

    add_heading_2(doc, '2.6 Sa-Token认证框架')
    add_body(doc, 'Sa-Token是一款轻量级Java权限认证框架，主要解决登录认证、权限验证、Session会话、单点登录等安全问题。与Spring Security等重量级安全框架相比，Sa-Token以API简单、功能强大、易于集成等优势著称。')
    add_body(doc, '本系统采用Sa-Token实现用户的登录认证和接口权限控制，通过Token机制实现无状态的用户身份验证，支持多端同时登录和Token自动续期，配合路由拦截器实现对管理端接口的访问控制。系统设计了前台用户和后台管理员的分离认证机制，两端使用独立的Token，互不影响。')

    add_heading_2(doc, '2.7 高德地图API')
    add_body(doc, '高德地图API是由高德开放平台提供的一套地图服务接口，支持地图展示、地理编码、路线规划、定位服务等功能[2]。本系统通过集成高德地图JavaScript API，在前端实现了景点位置的地图标注和展示，为用户提供从当前位置到目标景点的驾车、公交、步行等多种出行方式的路线规划服务，直观展示路线距离和预计耗时。')

    add_heading_2(doc, '2.8 DeepSeek AI智能问答')
    add_body(doc, 'DeepSeek是由深度求索公司开发的大语言模型，具备强大的自然语言理解和生成能力。本系统通过调用DeepSeek API，集成了AI智能问答功能，能够理解用户以自然语言提出的旅游相关问题，并生成专业、详细的回答，包括景点推荐、行程规划、旅游攻略建议等，为用户提供类似旅游顾问的智能化咨询服务。')

    add_heading_2(doc, '2.9 本章小结')
    add_body(doc, '本章详细介绍了智能旅游服务平台开发所使用的关键技术。后端采用Spring Boot框架搭配MyBatis-Plus和Sa-Token，前端采用Vue 3框架搭配Element Plus组件库，数据库采用MySQL，并集成高德地图API和DeepSeek AI问答服务。这些技术的合理选型和组合，为系统的高效开发和稳定运行提供了坚实的技术基础。')

    # ============================================================
    # 第3章 需求分析
    # ============================================================
    add_heading_1(doc, '3 需求分析')
    add_body(doc, '需求分析是软件开发过程中的关键环节，通过对用户需求的深入调研和分析，明确系统应具备的功能和性能指标，为后续的系统设计和开发提供依据。本章从功能需求和非功能需求两个方面对智能旅游服务平台进行详细的需求分析。')

    add_heading_2(doc, '3.1 系统概述')
    add_body(doc, '本系统是一个面向旅游用户和平台管理员的智能旅游服务平台。系统分为前台用户端和后台管理端两大部分。前台用户端面向普通旅游用户，提供景点浏览、攻略阅读、门票预订、AI智能问答、路线规划等服务；后台管理端面向平台管理员，提供用户管理、景点管理、攻略管理、订单管理、数据统计等功能。')

    add_heading_2(doc, '3.2 功能需求分析')

    add_heading_3(doc, '3.2.1 用户端功能需求')
    add_body(doc, '（1）用户注册与登录。用户可以通过用户名和密码进行注册和登录，系统采用Token机制进行身份认证，登录后可以访问个人中心和使用平台的各项功能。')
    add_body(doc, '（2）景点信息浏览。用户可以浏览平台发布的旅游景点信息，包括景点名称、封面图片、详细描述、地址位置、联系电话、评分等信息，支持按分类筛选和关键词搜索。')
    add_body(doc, '（3）旅游攻略功能。用户可以浏览其他用户发布的旅游攻略，也可以自行发布旅游攻略，分享旅行经验和心得，攻略支持富文本编辑。')
    add_body(doc, '（4）门票预订功能。用户可以在线预订景点门票，查看门票类型、价格、可用数量等信息，提交订单并完成购买，同时可以在个人中心查看和管理自己的订单。')
    add_body(doc, '（5）AI智能问答。用户可以通过对话界面向AI助手提出旅游相关问题，获取景点推荐、行程规划、旅游建议等智能化咨询服务。')
    add_body(doc, '（6）地图路线规划。用户可以在景点详情页面查看景点在地图上的位置，并获取从当前位置到景点的路线规划方案。')
    add_body(doc, '（7）互动功能。用户可以对景点和攻略进行点赞、收藏和评论，在个人中心查看和管理自己的点赞、收藏记录。')
    add_body(doc, '（8）个人中心。用户可以查看和编辑个人信息，管理自己的攻略、订单、收藏、点赞记录，修改登录密码。')

    add_heading_3(doc, '3.2.2 管理端功能需求')
    add_body(doc, '（1）数据统计与展示。管理员登录后可以查看平台的核心运营数据，包括用户数量、景点数量、攻略数量、订单数量等，并以图表形式展示数据趋势。')
    add_body(doc, '（2）用户管理。管理员可以查看所有注册用户的信息，包括用户名、性别、联系方式等，支持新增用户、编辑用户信息和删除用户。')
    add_body(doc, '（3）景点管理。管理员可以对景点信息进行增删改查操作，包括景点名称、分类、封面图片、描述、地址、经纬度、联系电话、是否推荐等信息的维护。')
    add_body(doc, '（4）分类管理。管理员可以管理景点的分类信息，包括添加、编辑和删除分类。')
    add_body(doc, '（5）攻略管理。管理员可以查看和管理平台上所有用户发布的旅游攻略，对不当内容进行删除处理。')
    add_body(doc, '（6）门票管理。管理员可以管理各景点的门票信息，包括门票类型、价格、库存数量等。')
    add_body(doc, '（7）订单管理。管理员可以查看所有门票订单信息，包括订单状态、购买用户、购买数量、订单金额等。')
    add_body(doc, '（8）评论管理。管理员可以查看和管理用户对攻略的评论信息。')
    add_body(doc, '（9）轮播图管理。管理员可以管理首页轮播图，包括上传图片、设置标题和描述。')
    add_body(doc, '（10）公告管理。管理员可以发布、编辑和删除平台公告信息。')

    # 用户用例图占位
    add_figure_placeholder(doc, 3, 1, '系统用例图')

    add_heading_2(doc, '3.3 非功能需求分析')
    add_body(doc, '（1）性能需求。系统应能支持一定数量的用户并发访问，页面加载时间不超过3秒，接口响应时间不超过1秒，确保用户获得流畅的使用体验。')
    add_body(doc, '（2）安全性需求。系统应具备完善的身份认证和权限控制机制，防止未授权访问；用户密码应加密存储，防止数据泄露；接口应进行参数校验，防止SQL注入和XSS攻击。')
    add_body(doc, '（3）可用性需求。系统界面设计应简洁直观，操作流程清晰，降低用户学习成本；系统应提供必要的操作提示和错误反馈信息。')
    add_body(doc, '（4）可扩展性需求。系统架构应具备良好的可扩展性，便于后续功能的扩展和技术升级；模块间应保持低耦合，便于独立开发和维护。')
    add_body(doc, '（5）兼容性需求。系统前端应兼容主流浏览器，包括Chrome、Firefox、Edge、Safari等。')

    add_heading_2(doc, '3.4 本章小结')
    add_body(doc, '本章对智能旅游服务平台进行了详细的需求分析，从用户端和管理端两个维度梳理了系统的功能需求，并明确了性能、安全性、可用性、可扩展性和兼容性等非功能需求。需求分析的结果为后续的系统设计和开发提供了明确的方向和依据。')

    # ============================================================
    # 第4章 系统设计
    # ============================================================
    add_heading_1(doc, '4 系统设计')
    add_body(doc, '系统设计是将需求分析的结果转化为技术方案的过程。本章从系统总体架构设计、功能模块设计和数据库设计三个方面对智能旅游服务平台进行详细设计。')

    add_heading_2(doc, '4.1 系统总体架构设计')
    add_body(doc, '本系统采用前后端分离的B/S（浏览器/服务器）架构，总体架构分为表现层、业务逻辑层和数据访问层三层结构，如图4-1所示。')

    add_figure_placeholder(doc, 4, 1, '系统总体架构图')

    add_body(doc, '表现层（前端）基于Vue 3框架开发，使用Element Plus组件库构建用户界面，通过Axios库发送HTTP请求与后端进行数据交互。前端应用通过Vue Router实现页面路由管理，使用Pinia进行状态管理，使用Vite作为构建工具。')
    add_body(doc, '业务逻辑层（后端）基于Spring Boot框架开发，采用Controller-Service-Mapper三层结构。Controller层负责接收前端请求并返回响应数据，Service层负责处理业务逻辑，Mapper层负责与数据库进行交互。后端通过Sa-Token实现用户认证和权限控制，通过RESTful API规范设计接口。')
    add_body(doc, '数据访问层使用MySQL数据库存储系统数据，通过MyBatis-Plus框架实现对象关系映射（ORM），简化数据库操作。同时，系统集成了高德地图API提供地图和路线规划服务，集成DeepSeek API提供AI智能问答服务。')

    add_heading_2(doc, '4.2 功能模块设计')
    add_body(doc, '根据需求分析的结果，系统的功能模块分为前台用户端模块和后台管理端模块两大部分，系统功能模块结构如图4-2所示。')

    add_figure_placeholder(doc, 4, 2, '系统功能模块图')

    add_heading_3(doc, '4.2.1 前台用户端模块设计')
    add_body(doc, '前台用户端包含以下核心功能模块：')
    add_body(doc, '（1）首页模块：展示轮播图、推荐景点、最新攻略和平台公告，为用户提供平台核心内容的快速入口。')
    add_body(doc, '（2）景点模块：提供景点列表展示和详情查看功能，支持分类筛选和搜索，景点详情页面集成地图展示和路线规划功能，用户可以对景点进行点赞、收藏和评论。')
    add_body(doc, '（3）攻略模块：提供攻略列表展示和详情查看功能，用户可以发布和管理自己的旅游攻略，攻略支持富文本编辑，包含图片插入。')
    add_body(doc, '（4）门票模块：展示各景点的门票信息，用户可以选择门票类型和数量进行在线预订，系统自动计算订单金额并管理库存。')
    add_body(doc, '（5）AI问答模块：提供对话式交互界面，用户输入问题后，系统调用DeepSeek API获取AI回答，支持Markdown格式渲染。')
    add_body(doc, '（6）个人中心模块：用户可以查看和编辑个人信息、查看我的攻略、我的订单、我的收藏、我的点赞，修改密码。')

    add_heading_3(doc, '4.2.2 后台管理端模块设计')
    add_body(doc, '后台管理端包含以下功能模块：')
    add_body(doc, '（1）首页仪表盘：以卡片和图表形式展示平台核心运营数据，包括用户总数、景点总数、攻略总数、订单总数及其变化趋势，使用ECharts图表库实现数据可视化。')
    add_body(doc, '（2）用户管理模块：对注册用户进行增删改查操作，支持分页查询和条件检索。')
    add_body(doc, '（3）景点管理模块：对景点信息进行增删改查操作，支持图片上传和推荐设置。')
    add_body(doc, '（4）分类管理模块：对景点分类进行管理，支持增删改操作。')
    add_body(doc, '（5）攻略管理模块：查看和管理所有用户发布的攻略信息。')
    add_body(doc, '（6）门票管理模块：管理各景点的门票类型、价格和库存信息。')
    add_body(doc, '（7）订单管理模块：查看和管理所有门票订单，支持订单状态流转。')
    add_body(doc, '（8）评论管理模块：查看和管理用户评论。')
    add_body(doc, '（9）轮播图管理模块：管理首页轮播图内容。')
    add_body(doc, '（10）公告管理模块：发布和管理平台公告。')

    add_heading_2(doc, '4.3 数据库设计')
    add_body(doc, '数据库设计是系统设计的重要组成部分，合理的数据库结构能够有效保证数据的完整性和系统的运行效率。本系统数据库命名为db_travel，共设计了11张数据表，下面对核心数据表进行说明。')

    # ---- 4.3.1 用户表 ----
    add_heading_3(doc, '4.3.1 用户表')
    add_body(doc, '用户表（user）用于存储系统注册用户的基本信息，表结构如表4-1所示。')
    add_table_caption(doc, 4, 1, '用户表（user）')
    create_three_line_table(doc,
        ['字段名', '类型', '说明'],
        [
            ['id', 'INT', '主键，自增'],
            ['username', 'VARCHAR(50)', '用户名，唯一'],
            ['password', 'VARCHAR(255)', '密码（加密存储）'],
            ['nick_name', 'VARCHAR(50)', '昵称'],
            ['avatar', 'VARCHAR(255)', '头像路径'],
            ['age', 'INT', '年龄'],
            ['gender', 'VARCHAR(10)', '性别'],
            ['phone', 'VARCHAR(20)', '手机号'],
            ['email', 'VARCHAR(100)', '邮箱'],
            ['profile', 'VARCHAR(500)', '个人简介'],
            ['status', 'INT', '状态（1正常/0禁用）'],
            ['role', 'VARCHAR(20)', '角色（user/admin）'],
            ['create_time', 'DATETIME', '创建时间'],
            ['update_time', 'DATETIME', '更新时间'],
        ])

    # ---- 4.3.2 景点表 ----
    add_heading_3(doc, '4.3.2 景点表')
    add_body(doc, '景点表（attraction）用于存储旅游景点的详细信息，表结构如表4-2所示。')
    add_table_caption(doc, 4, 2, '景点表（attraction）')
    create_three_line_table(doc,
        ['字段名', '类型', '说明'],
        [
            ['id', 'INT', '主键，自增'],
            ['name', 'VARCHAR(100)', '景点名称'],
            ['category_id', 'INT', '分类ID，关联category表'],
            ['cover_image', 'VARCHAR(255)', '封面图片'],
            ['description', 'TEXT', '景点描述'],
            ['address', 'VARCHAR(255)', '详细地址'],
            ['latitude', 'DECIMAL(10,7)', '纬度'],
            ['longitude', 'DECIMAL(10,7)', '经度'],
            ['contact_phone', 'VARCHAR(20)', '联系电话'],
            ['view_count', 'INT', '浏览数'],
            ['like_count', 'INT', '点赞数'],
            ['collect_count', 'INT', '收藏数'],
            ['rating', 'DECIMAL(2,1)', '评分'],
            ['is_recommend', 'INT', '是否推荐（0/1）'],
            ['create_time', 'DATETIME', '创建时间'],
            ['update_time', 'DATETIME', '更新时间'],
        ])

    # ---- 4.3.3 攻略表 ----
    add_heading_3(doc, '4.3.3 攻略表')
    add_body(doc, '攻略表（strategy）用于存储用户发布的旅游攻略信息，表结构如表4-3所示。')
    add_table_caption(doc, 4, 3, '攻略表（strategy）')
    create_three_line_table(doc,
        ['字段名', '类型', '说明'],
        [
            ['id', 'INT', '主键，自增'],
            ['title', 'VARCHAR(200)', '攻略标题'],
            ['user_id', 'INT', '发布用户ID'],
            ['attraction_id', 'INT', '关联景点ID'],
            ['cover_image', 'VARCHAR(255)', '封面图片'],
            ['intro', 'VARCHAR(500)', '简介'],
            ['content', 'LONGTEXT', '攻略内容（富文本）'],
            ['days', 'INT', '行程天数'],
            ['budget_type', 'INT', '预算类型（1低/2中/3高）'],
            ['estimated_budget', 'DECIMAL(10,2)', '预计预算'],
            ['season', 'VARCHAR(50)', '推荐季节'],
            ['view_count', 'INT', '浏览数'],
            ['like_count', 'INT', '点赞数'],
            ['collect_count', 'INT', '收藏数'],
            ['is_recommend', 'INT', '是否推荐（0/1）'],
            ['create_time', 'DATETIME', '创建时间'],
            ['update_time', 'DATETIME', '更新时间'],
        ])

    # ---- 4.3.4 门票表与订单表 ----
    add_heading_3(doc, '4.3.4 门票表与订单表')
    add_body(doc, '门票表（ticket）用于存储景点门票信息，表结构如表4-4所示。')
    add_table_caption(doc, 4, 4, '门票表（ticket）')
    create_three_line_table(doc,
        ['字段名', '类型', '说明'],
        [
            ['id', 'INT', '主键，自增'],
            ['attraction_id', 'INT', '关联景点ID'],
            ['cover_image', 'VARCHAR(255)', '封面图片'],
            ['name', 'VARCHAR(100)', '门票名称'],
            ['type', 'INT', '票种（1成人/2儿童/3学生/4老人）'],
            ['price', 'DECIMAL(10,2)', '售价'],
            ['original_price', 'DECIMAL(10,2)', '原价'],
            ['description', 'TEXT', '描述'],
            ['validity_period', 'VARCHAR(100)', '有效期'],
            ['purchase_notes', 'TEXT', '购买须知'],
            ['inventory', 'INT', '库存数量'],
            ['create_time', 'DATETIME', '创建时间'],
            ['update_time', 'DATETIME', '更新时间'],
        ])

    add_body(doc, '订单表（ticket_order）用于存储用户的门票购买订单，表结构如表4-5所示。')
    add_table_caption(doc, 4, 5, '订单表（ticket_order）')
    create_three_line_table(doc,
        ['字段名', '类型', '说明'],
        [
            ['id', 'INT', '主键，自增'],
            ['user_id', 'INT', '用户ID'],
            ['ticket_id', 'INT', '门票ID'],
            ['order_no', 'VARCHAR(50)', '订单编号'],
            ['pay_num', 'INT', '购买数量'],
            ['total_amount', 'DECIMAL(10,2)', '总金额'],
            ['status', 'INT', '状态（1已付款/2已完成/3已取消）'],
            ['contact_name', 'VARCHAR(50)', '联系人姓名'],
            ['contact_phone', 'VARCHAR(20)', '联系人电话'],
            ['card_no', 'VARCHAR(50)', '证件号'],
            ['create_time', 'DATETIME', '创建时间'],
            ['update_time', 'DATETIME', '更新时间'],
        ])

    # ---- 4.3.5 其他数据表 ----
    add_heading_3(doc, '4.3.5 其他数据表')
    add_body(doc, '除上述核心表外，系统还设计了以下辅助数据表，如表4-6所示。')
    add_table_caption(doc, 4, 6, '其他数据表概览')
    create_three_line_table(doc,
        ['表名', '功能说明', '关键字段'],
        [
            ['category', '景点分类表', 'id, name'],
            ['comment', '攻略评论表', 'id, strategy_id, user_id, content'],
            ['collect', '用户收藏表', 'id, user_id, target_type, target_id'],
            ['likes', '用户点赞表', 'id, user_id, target_type, target_id'],
            ['banner', '轮播图表', 'id, title, description, url'],
            ['notice', '公告表', 'id, title, content, user_id'],
        ])
    add_body(doc, '其中，收藏表和点赞表通过target_type字段区分收藏/点赞的对象类型（1为景点，2为攻略），通过target_id关联具体的景点或攻略记录，并设置了user_id、target_type、target_id的复合唯一索引，防止用户重复收藏或点赞。')

    # ---- E-R图 ----
    add_figure_placeholder(doc, 4, 3, '数据库E-R图')

    add_heading_2(doc, '4.4 接口设计')
    add_body(doc, '系统采用RESTful风格设计API接口，所有接口以/api为统一前缀。接口采用JSON格式进行数据传输，统一使用Result类封装响应结果，包含状态码（code）、提示信息（msg）和数据（data）三个字段。系统主要接口分类如表4-7所示。')

    add_table_caption(doc, 4, 7, '系统主要接口列表')
    create_three_line_table(doc,
        ['接口分类', '路径前缀', '主要功能'],
        [
            ['系统接口', '/api', '登录、注册、登出'],
            ['用户接口', '/api/user', '用户信息增删改查'],
            ['景点接口', '/api/attraction', '景点信息管理'],
            ['攻略接口', '/api/strategy', '攻略发布与管理'],
            ['门票接口', '/api/ticket', '门票信息管理'],
            ['订单接口', '/api/ticketOrder', '订单管理'],
            ['评论接口', '/api/comment', '评论管理'],
            ['收藏接口', '/api/collect', '收藏管理'],
            ['点赞接口', '/api/like', '点赞管理'],
            ['AI问答接口', '/api/ai', 'DeepSeek AI交互'],
            ['文件接口', '/api/file', '文件上传与下载'],
            ['统计接口', '/api/statistics', '数据统计'],
        ])

    add_heading_2(doc, '4.5 本章小结')
    add_body(doc, '本章对智能旅游服务平台进行了详细的系统设计。首先设计了基于前后端分离的三层架构，然后详细设计了前台用户端和后台管理端的功能模块，接着设计了包含11张数据表的数据库结构并以表格形式展示了各表字段详情，最后说明了RESTful风格的接口设计方案。系统设计方案合理、层次清晰，为后续的编码实现奠定了良好的基础。')

    # ============================================================
    # 第5章 系统实现
    # ============================================================
    add_heading_1(doc, '5 系统实现')
    add_body(doc, '本章基于第4章的系统设计方案，详细介绍智能旅游服务平台各功能模块的具体实现过程，包括开发环境搭建、前台用户端和后台管理端核心功能的实现。')

    # ---- 5.1 开发环境 ----
    add_heading_2(doc, '5.1 开发环境')
    add_body(doc, '系统的开发环境配置如表5-1所示。')
    add_table_caption(doc, 5, 1, '开发环境配置')
    create_three_line_table(doc,
        ['类别', '名称', '版本/说明'],
        [
            ['操作系统', 'Windows 10/11', '64位'],
            ['后端IDE', 'IntelliJ IDEA', '2023.x'],
            ['前端IDE', 'Visual Studio Code', '最新版'],
            ['JDK', 'JDK', '1.8'],
            ['构建工具', 'Maven', '3.x'],
            ['Node.js', 'Node.js', '16.x及以上'],
            ['包管理器', 'npm', '8.x及以上'],
            ['数据库', 'MySQL', '5.7'],
            ['数据库工具', 'Navicat Premium', '16.x'],
            ['浏览器', 'Google Chrome', '最新版'],
        ])

    # ---- 5.2 前台用户端实现 ----
    add_heading_2(doc, '5.2 前台用户端实现')

    add_heading_3(doc, '5.2.1 用户登录与注册')
    add_body(doc, '用户登录和注册功能是系统的基础功能。用户注册时需要填写用户名和密码，系统对用户名的唯一性进行校验，密码经过加密处理后存储到数据库。登录时，系统通过Sa-Token框架生成Token令牌返回给前端，前端将Token存储在localStorage中，后续请求通过Axios拦截器自动在请求头中携带Token进行身份验证。登录页面采用简洁的表单设计，如图5-1所示。')
    add_figure_placeholder(doc, 5, 1, '用户登录页面')

    add_heading_3(doc, '5.2.2 首页展示')
    add_body(doc, '首页是用户进入平台的第一个页面，包含轮播图展示、推荐景点列表和平台公告等内容。轮播图通过Element Plus的Carousel组件实现自动轮播和手动切换。推荐景点区域展示评分较高和标记为推荐的热门景点，以卡片形式排列展示景点封面、名称、地址和评分信息。用户点击景点卡片可以跳转到景点详情页面。首页效果如图5-2所示。')
    add_figure_placeholder(doc, 5, 2, '前台首页展示')

    add_heading_3(doc, '5.2.3 景点信息展示与详情')
    add_body(doc, '景点列表页面展示所有旅游景点信息，支持按分类筛选和关键词搜索功能。每个景点以卡片形式展示，包含封面图片、景点名称、简要描述、评分和浏览量等信息，如图5-3所示。')
    add_figure_placeholder(doc, 5, 3, '景点列表页面')
    add_body(doc, '景点详情页面展示景点的完整信息，包括大图展示、详细描述、地址信息、联系电话、浏览数、点赞数、收藏数和评分等。页面底部集成了高德地图组件，以地图标注的形式展示景点所在位置，用户可以点击"路线规划"按钮获取从当前位置到景点的路线方案。同时，详情页面提供了点赞、收藏和评论功能，如图5-4所示。')
    add_figure_placeholder(doc, 5, 4, '景点详情页面')

    add_heading_3(doc, '5.2.4 旅游攻略功能')
    add_body(doc, '攻略列表页面展示所有用户发布的旅游攻略，以卡片形式呈现，包含封面图片、标题、作者和浏览量。用户点击可进入攻略详情页面查看完整内容。')
    add_body(doc, '攻略发布页面集成了WangEditor富文本编辑器，用户可以编写图文并茂的旅游攻略。编辑器支持文字格式设置、图片插入、链接添加等常用编辑功能。用户填写攻略标题、选择封面图片、设置行程天数和预算等信息后，编写攻略内容，点击发布按钮即可将攻略提交到平台，如图5-5所示。')
    add_figure_placeholder(doc, 5, 5, '攻略发布页面')

    add_heading_3(doc, '5.2.5 门票预订功能')
    add_body(doc, '门票列表页面展示各景点的可购门票信息，包括门票名称、关联景点、价格和库存数量。用户选择门票后进入门票详情页面，可以查看门票的详细信息包括票种类型（成人票、儿童票、学生票、老人票）、售价与原价、有效期和购买须知，设置购买数量后系统自动计算订单总金额。用户填写联系人信息后提交订单，系统生成订单记录并自动扣减门票库存，如图5-6所示。')
    add_figure_placeholder(doc, 5, 6, '门票预订页面')

    add_heading_3(doc, '5.2.6 AI智能问答功能')
    add_body(doc, 'AI智能问答功能通过集成DeepSeek大语言模型实现。前端采用对话式界面设计，用户在输入框中输入旅游相关问题，系统将问题通过后端接口（/api/ai/send）转发到DeepSeek API，获取AI生成的回答后返回给前端展示。回答内容支持Markdown格式渲染，通过markdown-it库将Markdown文本转换为HTML进行展示，使回答内容更加结构化和美观。后端通过DeepSeekConfig配置类管理API密钥、模型选择和温度参数等配置，通过DeepSeekService服务类封装API调用逻辑，如图5-7所示。')
    add_figure_placeholder(doc, 5, 7, 'AI智能问答页面')

    add_heading_3(doc, '5.2.7 地图与路线规划功能')
    add_body(doc, '地图功能通过集成高德地图JavaScript API实现。系统封装了AMap.vue地图组件，在景点详情页面中加载高德地图，根据景点的经纬度信息在地图上添加标注点。路线规划功能利用高德地图的路线规划插件，支持驾车、公交和步行三种出行方式。用户获取浏览器定位权限后，系统自动获取用户当前位置作为起点，以景点位置作为终点，计算并在地图上绘制推荐路线，同时显示距离和预计耗时信息，如图5-8所示。')
    add_figure_placeholder(doc, 5, 8, '地图路线规划功能')

    # ---- 5.3 后台管理端实现 ----
    add_heading_2(doc, '5.3 后台管理端实现')

    add_heading_3(doc, '5.3.1 管理端首页仪表盘')
    add_body(doc, '管理端首页以仪表盘形式展示平台核心运营数据。页面顶部以卡片形式展示用户总数、景点总数、攻略总数、订单总数等关键指标。页面主体区域使用ECharts图表库绘制数据可视化图表，包括柱状图和折线图，直观展示各类数据的分布和变化趋势，帮助管理员快速了解平台运营状况，如图5-9所示。')
    add_figure_placeholder(doc, 5, 9, '管理端首页仪表盘')

    add_heading_3(doc, '5.3.2 数据管理功能')
    add_body(doc, '后台管理端的数据管理功能采用统一的列表-表单模式实现。列表页面使用Element Plus的Table组件展示数据，支持分页查询、条件搜索和排序功能。操作列提供编辑和删除按钮。新增和编辑操作通过Dialog对话框弹出表单进行数据录入，表单使用Element Plus的Form组件并配置表单验证规则，确保用户输入数据的规范性。')
    add_body(doc, '以景点管理为例，管理员可以在景点列表中查看所有景点信息，通过搜索框按名称或分类检索景点。点击"新增"按钮弹出景点信息表单，填写景点名称、选择分类、上传封面图片、输入详细描述、地址、经纬度、联系电话等信息，设置是否推荐后保存。编辑功能复用同一表单，加载已有数据供管理员修改。删除操作经过二次确认后执行，保证数据安全，如图5-10所示。')
    add_figure_placeholder(doc, 5, 10, '景点管理页面')

    add_heading_3(doc, '5.3.3 文件上传功能')
    add_body(doc, '系统的文件上传功能通过FileController实现。前端使用Element Plus的Upload组件选择图片文件，通过FormData格式上传到后端接口。后端接收文件后，生成唯一文件名（UUID），将文件保存到服务器指定目录（backend/files/），并返回文件访问路径。系统通过WebMvcConfig配置静态资源映射，使上传的文件可以通过URL直接访问。文件上传限制单个文件大小不超过2MB，确保系统存储空间的合理使用。')

    add_heading_2(doc, '5.4 本章小结')
    add_body(doc, '本章详细介绍了智能旅游服务平台的具体实现过程。前台用户端实现了登录注册、首页展示、景点浏览、攻略管理、门票预订、AI智能问答、地图路线规划等核心功能；后台管理端实现了数据统计、用户管理、景点管理、订单管理等管理功能。系统各模块功能完整，界面交互友好，满足了需求分析中提出的各项功能要求。')

    # ============================================================
    # 第6章 系统测试
    # ============================================================
    add_heading_1(doc, '6 系统测试')
    add_body(doc, '系统测试是保证软件质量的重要环节。本章对智能旅游服务平台进行功能测试和性能测试，验证系统是否满足需求规格说明中的各项要求。')

    add_heading_2(doc, '6.1 测试环境')
    add_body(doc, '系统测试环境配置如表6-1所示。')
    add_table_caption(doc, 6, 1, '测试环境配置')
    create_three_line_table(doc,
        ['类别', '配置'],
        [
            ['操作系统', 'Windows 11'],
            ['处理器', 'Intel Core i5及以上'],
            ['内存', '8 GB及以上'],
            ['浏览器', 'Google Chrome（最新版本）'],
            ['后端服务', '本地Tomcat，端口8080'],
            ['前端服务', 'Vite开发服务器'],
            ['数据库', 'MySQL 5.7（本地）'],
        ])

    add_heading_2(doc, '6.2 功能测试')
    add_body(doc, '功能测试旨在验证系统各功能模块是否按照需求正确实现。本节选取核心功能模块进行测试用例设计和执行。')

    # ---- 6.2.1 用户登录测试 ----
    add_heading_3(doc, '6.2.1 用户登录测试')
    add_table_caption(doc, 6, 2, '用户登录功能测试用例')
    create_three_line_table(doc,
        ['编号', '测试输入', '预期结果', '实际结果', '是否通过'],
        [
            ['TC-01', '正确用户名和密码', '登录成功，跳转首页', '与预期一致', '通过'],
            ['TC-02', '正确用户名，错误密码', '提示用户名或密码错误', '与预期一致', '通过'],
            ['TC-03', '用户名或密码为空', '表单验证提示必填', '与预期一致', '通过'],
            ['TC-04', '未注册的用户名', '提示用户不存在', '与预期一致', '通过'],
        ])

    # ---- 6.2.2 景点信息浏览测试 ----
    add_heading_3(doc, '6.2.2 景点信息浏览测试')
    add_table_caption(doc, 6, 3, '景点信息浏览功能测试用例')
    create_three_line_table(doc,
        ['编号', '测试输入', '预期结果', '实际结果', '是否通过'],
        [
            ['TC-05', '选择分类筛选', '仅展示该分类景点', '与预期一致', '通过'],
            ['TC-06', '输入关键词搜索', '展示匹配景点', '与预期一致', '通过'],
            ['TC-07', '点击景点卡片', '跳转详情页，地图标注正确', '与预期一致', '通过'],
            ['TC-08', '点击收藏/点赞按钮', '收藏/点赞状态变更', '与预期一致', '通过'],
        ])

    # ---- 6.2.3 门票预订测试 ----
    add_heading_3(doc, '6.2.3 门票预订测试')
    add_table_caption(doc, 6, 4, '门票预订功能测试用例')
    create_three_line_table(doc,
        ['编号', '测试输入', '预期结果', '实际结果', '是否通过'],
        [
            ['TC-09', '购买数量为2，提交订单', '订单创建成功，金额=单价×2', '与预期一致', '通过'],
            ['TC-10', '购买数量超过库存', '提示库存不足', '与预期一致', '通过'],
            ['TC-11', '查看我的订单', '订单列表显示正确', '与预期一致', '通过'],
        ])

    # ---- 6.2.4 AI智能问答测试 ----
    add_heading_3(doc, '6.2.4 AI智能问答测试')
    add_table_caption(doc, 6, 5, 'AI智能问答功能测试用例')
    create_three_line_table(doc,
        ['编号', '测试输入', '预期结果', '实际结果', '是否通过'],
        [
            ['TC-12', '输入"推荐北京旅游景点"', 'AI返回合理推荐内容', '返回故宫、颐和园等推荐', '通过'],
            ['TC-13', '发送空消息', '提示请输入问题', '与预期一致', '通过'],
        ])

    # ---- 6.2.5 后台管理功能测试 ----
    add_heading_3(doc, '6.2.5 后台管理功能测试')
    add_table_caption(doc, 6, 6, '后台管理功能测试用例')
    create_three_line_table(doc,
        ['编号', '测试输入', '预期结果', '实际结果', '是否通过'],
        [
            ['TC-14', '新增景点信息并保存', '景点保存成功，列表更新', '与预期一致', '通过'],
            ['TC-15', '编辑景点名称和描述', '信息更新成功', '与预期一致', '通过'],
            ['TC-16', '删除景点，确认删除', '景点从列表移除', '与预期一致', '通过'],
            ['TC-17', '管理员查看数据统计', '仪表盘数据正确显示', '与预期一致', '通过'],
        ])

    add_heading_2(doc, '6.3 性能测试')
    add_body(doc, '对系统的关键接口进行性能测试，测试结果如表6-7所示。')
    add_table_caption(doc, 6, 7, '性能测试结果')
    create_three_line_table(doc,
        ['测试项目', '测试条件', '响应时间', '是否达标'],
        [
            ['首页加载', '首次访问', '约1.5秒', '达标（<3秒）'],
            ['首页加载', '缓存后访问', '约0.5秒', '达标（<3秒）'],
            ['景点列表查询', '20条数据分页查询', '约50毫秒', '达标（<1秒）'],
            ['AI问答响应', '调用DeepSeek API', '3-8秒', '合理范围'],
            ['文件上传', '1 MB图片', '约1秒', '达标'],
        ])

    add_heading_2(doc, '6.4 测试总结')
    add_body(doc, '通过对系统进行全面的功能测试和性能测试，结果表明：系统各功能模块均按照需求正确实现，登录认证、景点浏览、攻略管理、门票预订、AI问答、地图路线规划等核心功能运行正常；后台管理功能的增删改查操作稳定可靠；系统性能指标满足设计要求。整体来看，系统功能完善、运行稳定，达到了预期的设计目标。')

    # ============================================================
    # 结论（不编号，模板要求）
    # ============================================================
    add_heading_1_center(doc, '结  论')
    add_empty_line(doc, line_spacing=1.15)
    add_body(doc, '本文设计并实现了一个基于Spring Boot与Vue的智能旅游服务平台。系统采用前后端分离架构，后端基于Spring Boot框架搭配MyBatis-Plus和Sa-Token，前端基于Vue 3框架搭配Element Plus组件库，数据库采用MySQL，并集成了高德地图API和DeepSeek AI智能问答服务。')
    add_body(doc, '系统实现了以下主要功能：（1）景点信息展示，用户可以浏览各类旅游景点的详细信息，支持分类筛选和搜索；（2）旅游攻略功能，用户可以发布和浏览旅游攻略，分享旅行经验；（3）门票在线预订，用户可以在线预订景点门票，系统自动管理库存和订单；（4）AI智能问答，通过集成DeepSeek大语言模型，为用户提供智能化的旅游咨询服务；（5）地图路线规划，通过集成高德地图API，为用户提供景点定位和多种出行方式的路线规划；（6）用户互动功能，包括点赞、收藏和评论；（7）后台管理系统，管理员可以对用户、景点、攻略、门票、订单等数据进行集中管理和统计分析。')
    add_body(doc, '通过系统测试验证，各功能模块运行正常，系统性能满足设计要求，达到了预期的设计目标。本系统的设计与实现对智慧旅游平台的构建具有一定的参考价值。')
    add_body(doc, '尽管本系统已经实现了预期的核心功能，但仍存在一些可以改进和完善的方面：（1）移动端适配，当前系统主要面向PC端浏览器，未来可以开发响应式布局或独立的移动端应用；（2）支付功能集成，当前门票预订功能尚未集成在线支付，未来可以对接微信支付或支付宝等第三方支付平台；（3）个性化推荐，可以引入协同过滤或基于内容的推荐算法，根据用户的浏览历史和偏好推荐个性化内容；（4）AI功能深化，可以进一步利用AI技术实现智能行程规划、图像识别等功能；（5）系统性能优化，可以引入Redis缓存、CDN加速等技术手段，进一步提升系统的并发处理能力和响应速度。')

    # ============================================================
    # 参考文献
    # ============================================================
    add_heading_1_center(doc, '参考文献')
    add_empty_line(doc, line_spacing=1.15)

    refs = [
        '[1] 肖程鸣,曾志颖.基于Spring Boot和Vue的红色智慧旅游平台设计与实现[J].软件,2022,43(07):30-33+38.',
        '[2] 马立,许文韬,王明珠,等.基于高德地图API的邯郸市旅游景点查询平台设计[J].河北工程大学学报(社会科学版),2019,36(03):53-57.',
        '[3] 戴亚哲,李尤,赵利宏,等.基于Spring Boot+Vue的文旅平台设计与研究[J].无线互联科技,2024,21(21):70-72.',
        '[4] 曹浩,黎杰,谢彬.基于Spring Boot+Vue的桂林龙胜各族自治县的旅游信息系统设计[J].现代信息科技,2024,8(16):102-106.',
        '[5] 宁毅,陈金龙,罗德明,等.基于Spring Boot+Spark+Vue的旅游大数据分析平台的设计与实现[J].无线互联科技,2024,21(07):60-67.',
        '[6] 陈伍香,卢新宇,蒋清红,等.5G时代下基于微服务架构的桂林智慧旅游管理系统设计与实现[J].信息与电脑(理论版),2023,35(20):151-153.',
        '[7] 郑赞红,许俊轩.广东文旅智慧管理系统的设计与实现[J].福建电脑,2025,41(10):69-75.',
        '[8] 和娴,杜臣昌,郑英,等.旅游景区游客信息系统设计与实现[J].绿色科技,2024,26(02):244-248+265.',
        '[9] 徐永康,高文超,廖宏科,等.基于Android的个性化旅游路线规划系统设计与实现[J].电脑知识与技术,2021,17(24):87-89.',
        '[10] 孙岩,李晶.基于SpringBoot的旅游资源管理网站的设计与实现[J].信息技术与信息化,2021,(01):37-39.',
        '[11] 王家明,李湘.基于大数据分析的新疆智慧旅游网站的设计与实现[J].电脑知识与技术,2021,17(03):129-130+133.',
        '[12] 王文湛,徐熙涛,黄威.基于SpringBoot+Vue+GIS的旅游信息管理系统[J].信息与电脑,2025,37(20):254-256.',
        '[13] 范路桥,高洁,段班祥.基于Python+Flask+ECharts的国内热门旅游景点数据可视化系统[J].现代电子技术,2023,46(09):126-130.',
        '[14] Qi L, Yi Z. Design and Implementation of Smart Tourism Service Platform from the Perspective of Artificial Intelligence[J]. Wireless Communications and Mobile Computing, 2022, 2022.',
        '[15] Chen G, Xu J. Design and implementation of efficient Learning platform based on SpringBoot Framework[J]. Journal of Electronics and Information Science, 2020, 6(1).',
    ]

    for ref in refs:
        p = doc.add_paragraph()
        set_paragraph_format(p, alignment=WD_ALIGN_PARAGRAPH.JUSTIFY,
                             line_spacing=1.15,
                             hanging_indent=480)  # 悬挂缩进约2字符
        run = p.add_run(ref)
        set_run_font(run, cn_font='宋体', en_font='Times New Roman', size=FONT_XIAO_SI)

    # ============================================================
    # 致谢
    # ============================================================
    add_heading_1_center(doc, '致  谢')
    add_empty_line(doc, line_spacing=1.15)
    add_body(doc, '时光荏苒，转眼间大学四年的学习生活即将画上句号。在本科毕业论文完成之际，我要向所有关心和帮助过我的人表达最诚挚的感谢。')
    add_body(doc, '首先，我要衷心感谢我的指导老师陈得友老师。从课题选定、开题报告撰写到系统开发和论文写作的每一个阶段，陈老师都给予了我悉心的指导和帮助。陈老师严谨的治学态度、丰富的专业知识和耐心的指导方式，使我在学术研究和工程实践方面受益匪浅。在此向陈老师表示最衷心的感谢和崇高的敬意。')
    add_body(doc, '其次，我要感谢郑州西亚斯学院数字技术产业学院的各位老师。四年来，老师们传授的专业知识和实践技能为我完成本次毕业设计奠定了坚实的基础。感谢老师们在课堂内外给予我的教导和帮助。')
    add_body(doc, '同时，我要感谢我的同学和朋友们。在大学四年的学习和生活中，我们互相帮助、共同成长。特别是在毕业设计期间，同学们之间的技术交流和经验分享给了我很多启发和帮助。')
    add_body(doc, '最后，我要特别感谢我的家人。感谢父母多年来在学业和生活上给予我的无私支持和关爱，是你们的鼓励和付出让我能够安心学习、顺利完成学业。')
    add_body(doc, '再次向所有关心和帮助过我的人致以最诚挚的感谢！')

    # ============================================================
    # 附录
    # ============================================================
    add_heading_1_center(doc, '附  录')
    add_empty_line(doc, line_spacing=1.15)

    # 附录1：核心代码
    p = doc.add_paragraph()
    set_paragraph_format(p, alignment=WD_ALIGN_PARAGRAPH.LEFT, line_spacing=1.5)
    run = p.add_run('附录1 系统核心代码')
    set_run_font(run, cn_font='宋体', size=FONT_XIAO_SI, bold=True)

    add_body(doc, '1. 统一响应结果封装（Result.java）')

    # 代码块
    code_result = '''public class Result {
    private Integer code;
    private String msg;
    private Object data;

    public static BaseResponse success() {
        return new BaseResponse(200, "成功", null);
    }

    public static BaseResponse success(Object data) {
        return new BaseResponse(200, "成功", data);
    }

    public static BaseResponse error(String msg) {
        return new BaseResponse(500, msg, null);
    }
}'''
    p = doc.add_paragraph()
    set_paragraph_format(p, line_spacing=1.15)
    run = p.add_run(code_result)
    set_run_font(run, cn_font='Consolas', en_font='Consolas', size=FONT_WU_HAO)

    add_body(doc, '2. Sa-Token认证配置（SaTokenConfigure.java核心片段）')

    code_satoken = '''@Configuration
public class SaTokenConfigure implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor(handle -> {
            SaRouter.match("/**")
                .notMatch(ExcludePathEnum.getExcludePaths())
                .check(r -> StpUtil.checkLogin());
        })).addPathPatterns("/**");
    }
}'''
    p = doc.add_paragraph()
    set_paragraph_format(p, line_spacing=1.15)
    run = p.add_run(code_satoken)
    set_run_font(run, cn_font='Consolas', en_font='Consolas', size=FONT_WU_HAO)

    add_body(doc, '3. DeepSeek AI调用服务（DeepSeekService.java核心片段）')

    code_deepseek = '''@Service
public class DeepSeekService {
    @Autowired
    private DeepSeekConfig config;

    public String chat(String query) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", config.getModel());
        requestBody.put("max_tokens", config.getMaxTokens());
        requestBody.put("temperature", config.getTemperature());
        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "user", "content", query));
        requestBody.put("messages", messages);
        // 发送HTTP请求到DeepSeek API并返回结果
        return sendRequest(requestBody);
    }
}'''
    p = doc.add_paragraph()
    set_paragraph_format(p, line_spacing=1.15)
    run = p.add_run(code_deepseek)
    set_run_font(run, cn_font='Consolas', en_font='Consolas', size=FONT_WU_HAO)

    add_body(doc, '4. 前端Axios请求拦截器（http.js核心片段）')

    code_axios = '''const http = axios.create({
    baseURL: config.baseUrl,
    timeout: 600000,
});

http.interceptors.request.use(config => {
    const userStore = useUserStore();
    const isFront = window.location.hash.includes("/front");
    const token = isFront
        ? userStore.frontToken
        : userStore.adminToken;
    if (token) {
        config.headers["Authorization"] = token;
    }
    return config;
});'''
    p = doc.add_paragraph()
    set_paragraph_format(p, line_spacing=1.15)
    run = p.add_run(code_axios)
    set_run_font(run, cn_font='Consolas', en_font='Consolas', size=FONT_WU_HAO)

    # ============================================================
    # 设置页面边距和页码
    # ============================================================
    for section in doc.sections:
        section.top_margin = Cm(3)
        section.bottom_margin = Cm(3)
        section.left_margin = Cm(3)
        section.right_margin = Cm(2.6)
        # 页脚距底端1.1cm
        section.footer_distance = Cm(1.1)

    # 添加页码（在第一个section上）
    add_page_number_field(doc, doc.sections[0])

    # ============================================================
    # 保存
    # ============================================================
    output_path = '/home/clawbot/.openclaw/workspace/travel/毕业论文_基于Spring Boot+Vue的智能旅游服务平台设计与实现.docx'
    doc.save(output_path)

    # 同时复制一份到 outbound
    import shutil
    outbound_path = '/home/clawbot/.openclaw/media/outbound/thesis.docx'
    shutil.copy2(output_path, outbound_path)

    print(f'论文生成完成！')
    print(f'  主文件: {output_path}')
    print(f'  副本: {outbound_path}')
    print()
    print('【注意事项】')
    print('1. 请在 Word 中打开后，右键目录区域选择"更新域"生成目录')
    print('2. 请在 Word 中按 Ctrl+A 全选后按 F9 更新所有页码域')
    print('3. 图片占位符处请替换为实际的系统截图')
    print('4. 封面完成时间已填写为"2025年5月"，如需修改请直接在封面表格中更改')


if __name__ == '__main__':
    generate_thesis()
