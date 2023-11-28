// This is a generated file. Not intended for manual editing.
package org.droneDSL.cli.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;

import static com.intellij.lang.parser.GeneratedParserUtilBase.*;

import com.intellij.lang.parser.GeneratedParserUtilBase;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiParser;
import com.intellij.lang.LightPsiParser;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class BotPsiParser implements PsiParser, LightPsiParser {

  public ASTNode parse(IElementType t, PsiBuilder b) {
    parseLight(t, b);
    return b.getTreeBuilt();
  }

  public void parseLight(IElementType t, PsiBuilder b) {
    boolean r;
    b = adapt_builder_(t, b, this, null);
    Marker m = enter_section_(b, 0, _COLLAPSE_, null);
    r = parse_root_(t, b);
    exit_section_(b, 0, m, t, r, true, TRUE_CONDITION);
  }

  protected boolean parse_root_(IElementType t, PsiBuilder b) {
    return parse_root_(t, b, 0);
  }

  static boolean parse_root_(IElementType t, PsiBuilder b, int l) {
    return file(b, l + 1);
  }

  /* ********************************************************** */
  // ID <<coloned attribute_expr>>
  public static boolean attribute(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "attribute")) return false;
    if (!GeneratedParserUtilBase.nextTokenIs(b, BotPsiElementTypes.ID)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = GeneratedParserUtilBase.consumeToken(b, BotPsiElementTypes.ID);
    r = r && coloned(b, l + 1, BotPsiParser::attribute_expr);
    exit_section_(b, m, BotPsiElementTypes.ATTRIBUTE, r);
    return r;
  }

  /* ********************************************************** */
  // NUMBER | name | <<square_bracked <<commaSep <<paren waypoint>> >> >>
  public static boolean attribute_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "attribute_expr")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, BotPsiElementTypes.ATTRIBUTE_EXPR, "<attribute expr>");
    r = GeneratedParserUtilBase.consumeToken(b, BotPsiElementTypes.NUMBER);
    if (!r) r = name(b, l + 1);
    if (!r) r = square_bracked(b, l + 1, attribute_expr_2_0_parser_);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // attribute*
  static boolean attributes(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "attributes")) return false;
    while (true) {
      int c = current_position_(b);
      if (!attribute(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "attributes", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // LBRACE <<param>> RBRACE
  static boolean braced(PsiBuilder b, int l, Parser _param) {
    if (!recursion_guard_(b, l, "braced")) return false;
    if (!GeneratedParserUtilBase.nextTokenIs(b, BotPsiElementTypes.LBRACE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = GeneratedParserUtilBase.consumeToken(b, BotPsiElementTypes.LBRACE);
    r = r && _param.parse(b, l);
    r = r && GeneratedParserUtilBase.consumeToken(b, BotPsiElementTypes.RBRACE);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // COLON <<param>>
  static boolean coloned(PsiBuilder b, int l, Parser _param) {
    if (!recursion_guard_(b, l, "coloned")) return false;
    if (!GeneratedParserUtilBase.nextTokenIs(b, BotPsiElementTypes.COLON)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = GeneratedParserUtilBase.consumeToken(b, BotPsiElementTypes.COLON);
    r = r && _param.parse(b, l);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  static Parser commaSep_$(Parser _param) {
    return (b, l) -> commaSep(b, l + 1, _param);
  }

  // <<param>> (COMMA <<param>>) *
  static boolean commaSep(PsiBuilder b, int l, Parser _param) {
    if (!recursion_guard_(b, l, "commaSep")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = _param.parse(b, l);
    r = r && commaSep_1(b, l + 1, _param);
    exit_section_(b, m, null, r);
    return r;
  }

  // (COMMA <<param>>) *
  private static boolean commaSep_1(PsiBuilder b, int l, Parser _param) {
    if (!recursion_guard_(b, l, "commaSep_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!commaSep_1_0(b, l + 1, _param)) break;
      if (!empty_element_parsed_guard_(b, "commaSep_1", c)) break;
    }
    return true;
  }

  // COMMA <<param>>
  private static boolean commaSep_1_0(PsiBuilder b, int l, Parser _param) {
    if (!recursion_guard_(b, l, "commaSep_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = GeneratedParserUtilBase.consumeToken(b, BotPsiElementTypes.COMMA);
    r = r && _param.parse(b, l);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // ID <<paren NUMBER>>?
  public static boolean cond(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "cond")) return false;
    if (!GeneratedParserUtilBase.nextTokenIs(b, BotPsiElementTypes.ID)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = GeneratedParserUtilBase.consumeToken(b, BotPsiElementTypes.ID);
    r = r && cond_1(b, l + 1);
    exit_section_(b, m, BotPsiElementTypes.COND, r);
    return r;
  }

  // <<paren NUMBER>>?
  private static boolean cond_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "cond_1")) return false;
    paren(b, l + 1, NUMBER_parser_);
    return true;
  }

  /* ********************************************************** */
  // task mission
  static boolean file(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "file")) return false;
    if (!GeneratedParserUtilBase.nextTokenIs(b, BotPsiElementTypes.TASK_KW)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = task(b, l + 1);
    r = r && mission(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // MISSION_KW <<braced mission_content>>
  public static boolean mission(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "mission")) return false;
    if (!GeneratedParserUtilBase.nextTokenIs(b, BotPsiElementTypes.MISSION_KW)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = GeneratedParserUtilBase.consumeToken(b, BotPsiElementTypes.MISSION_KW);
    r = r && braced(b, l + 1, BotPsiParser::mission_content);
    exit_section_(b, m, BotPsiElementTypes.MISSION, r);
    return r;
  }

  /* ********************************************************** */
  // mission_start_decl mission_transition*
  public static boolean mission_content(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "mission_content")) return false;
    if (!GeneratedParserUtilBase.nextTokenIs(b, BotPsiElementTypes.MISSION_START_KW)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = mission_start_decl(b, l + 1);
    r = r && mission_content_1(b, l + 1);
    exit_section_(b, m, BotPsiElementTypes.MISSION_CONTENT, r);
    return r;
  }

  // mission_transition*
  private static boolean mission_content_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "mission_content_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!mission_transition(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "mission_content_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // MISSION_START_KW <<braced task_name>>
  public static boolean mission_start_decl(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "mission_start_decl")) return false;
    if (!GeneratedParserUtilBase.nextTokenIs(b, BotPsiElementTypes.MISSION_START_KW)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = GeneratedParserUtilBase.consumeToken(b, BotPsiElementTypes.MISSION_START_KW);
    r = r && braced(b, l + 1, BotPsiParser::task_name);
    exit_section_(b, m, BotPsiElementTypes.MISSION_START_DECL, r);
    return r;
  }

  /* ********************************************************** */
  // TRANSITION_KW <<paren cond>> task_name ARROW task_name
  public static boolean mission_transition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "mission_transition")) return false;
    if (!GeneratedParserUtilBase.nextTokenIs(b, BotPsiElementTypes.TRANSITION_KW)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = GeneratedParserUtilBase.consumeToken(b, BotPsiElementTypes.TRANSITION_KW);
    r = r && paren(b, l + 1, BotPsiParser::cond);
    r = r && task_name(b, l + 1);
    r = r && GeneratedParserUtilBase.consumeToken(b, BotPsiElementTypes.ARROW);
    r = r && task_name(b, l + 1);
    exit_section_(b, m, BotPsiElementTypes.MISSION_TRANSITION, r);
    return r;
  }

  /* ********************************************************** */
  // ID
  public static boolean name(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "name")) return false;
    if (!GeneratedParserUtilBase.nextTokenIs(b, BotPsiElementTypes.ID)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = GeneratedParserUtilBase.consumeToken(b, BotPsiElementTypes.ID);
    exit_section_(b, m, BotPsiElementTypes.NAME, r);
    return r;
  }

  /* ********************************************************** */
  static Parser paren_$(Parser _param) {
    return (b, l) -> paren(b, l + 1, _param);
  }

  // LPAREN <<param>> RPAREN
  public static boolean paren(PsiBuilder b, int l, Parser _param) {
    if (!recursion_guard_(b, l, "paren")) return false;
    if (!GeneratedParserUtilBase.nextTokenIs(b, BotPsiElementTypes.LPAREN)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = GeneratedParserUtilBase.consumeToken(b, BotPsiElementTypes.LPAREN);
    r = r && _param.parse(b, l);
    r = r && GeneratedParserUtilBase.consumeToken(b, BotPsiElementTypes.RPAREN);
    exit_section_(b, m, BotPsiElementTypes.PAREN, r);
    return r;
  }

  /* ********************************************************** */
  // LSQUA <<param>> RSQUA
  public static boolean square_bracked(PsiBuilder b, int l, Parser _param) {
    if (!recursion_guard_(b, l, "square_bracked")) return false;
    if (!GeneratedParserUtilBase.nextTokenIs(b, BotPsiElementTypes.LSQUA)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = GeneratedParserUtilBase.consumeToken(b, BotPsiElementTypes.LSQUA);
    r = r && _param.parse(b, l);
    r = r && GeneratedParserUtilBase.consumeToken(b, BotPsiElementTypes.RSQUA);
    exit_section_(b, m, BotPsiElementTypes.SQUARE_BRACKED, r);
    return r;
  }

  /* ********************************************************** */
  // TASK_KW <<braced task_decl*>>
  public static boolean task(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "task")) return false;
    if (!GeneratedParserUtilBase.nextTokenIs(b, BotPsiElementTypes.TASK_KW)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = GeneratedParserUtilBase.consumeToken(b, BotPsiElementTypes.TASK_KW);
    r = r && braced(b, l + 1, BotPsiParser::task_1_0);
    exit_section_(b, m, BotPsiElementTypes.TASK, r);
    return r;
  }

  // task_decl*
  private static boolean task_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "task_1_0")) return false;
    while (true) {
      int c = current_position_(b);
      if (!task_decl(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "task_1_0", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // <<braced <<commaSep attributes>>>>
  public static boolean task_body(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "task_body")) return false;
    if (!GeneratedParserUtilBase.nextTokenIs(b, BotPsiElementTypes.LBRACE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = braced(b, l + 1, task_body_0_0_parser_);
    exit_section_(b, m, BotPsiElementTypes.TASK_BODY, r);
    return r;
  }

  /* ********************************************************** */
  // (TASK_DETECT_KW | TASK_TRACK_KW) task_name task_body
  public static boolean task_decl(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "task_decl")) return false;
    if (!nextTokenIs(b, "<task decl>", BotPsiElementTypes.TASK_DETECT_KW, BotPsiElementTypes.TASK_TRACK_KW)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, BotPsiElementTypes.TASK_DECL, "<task decl>");
    r = task_decl_0(b, l + 1);
    r = r && task_name(b, l + 1);
    r = r && task_body(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // TASK_DETECT_KW | TASK_TRACK_KW
  private static boolean task_decl_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "task_decl_0")) return false;
    boolean r;
    r = GeneratedParserUtilBase.consumeToken(b, BotPsiElementTypes.TASK_DETECT_KW);
    if (!r) r = GeneratedParserUtilBase.consumeToken(b, BotPsiElementTypes.TASK_TRACK_KW);
    return r;
  }

  /* ********************************************************** */
  // name
  public static boolean task_name(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "task_name")) return false;
    if (!GeneratedParserUtilBase.nextTokenIs(b, BotPsiElementTypes.ID)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = name(b, l + 1);
    exit_section_(b, m, BotPsiElementTypes.TASK_NAME, r);
    return r;
  }

  /* ********************************************************** */
  // NUMBER COMMA NUMBER COMMA NUMBER
  public static boolean waypoint(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "waypoint")) return false;
    if (!GeneratedParserUtilBase.nextTokenIs(b, BotPsiElementTypes.NUMBER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, BotPsiElementTypes.NUMBER, BotPsiElementTypes.COMMA, BotPsiElementTypes.NUMBER, BotPsiElementTypes.COMMA, BotPsiElementTypes.NUMBER);
    exit_section_(b, m, BotPsiElementTypes.WAYPOINT, r);
    return r;
  }

  static final Parser NUMBER_parser_ = (b, l) -> GeneratedParserUtilBase.consumeToken(b, BotPsiElementTypes.NUMBER);

  private static final Parser attribute_expr_2_0_0_parser_ = paren_$(BotPsiParser::waypoint);
  private static final Parser attribute_expr_2_0_parser_ = commaSep_$(attribute_expr_2_0_0_parser_);
  private static final Parser task_body_0_0_parser_ = commaSep_$(BotPsiParser::attributes);
}
