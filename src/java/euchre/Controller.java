/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package euchre;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


public class Controller extends HttpServlet {
    private Game game;
    private Set<String> bootList;
    
    public Controller() {
        game = new Game();
        bootList = new HashSet<String>();
    }
    
    private void xmlcards(PrintWriter out, String top, List<Card> cards) {
        out.println("<" + top + ">");
        for(Card card : cards) {
            if(card == null) continue;
            out.println("<card>");
            out.println("<suit>" + card.suit + "</suit>");
            out.println("<type>" + card.type + "</type>");
            out.println("</card>");
        }
        out.println("</" + top + ">");
    }
    
    protected void processRequest(HttpServletRequest request, 
            HttpServletResponse response, boolean post)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        String username = (String)session.getAttribute("username");
        if(game.shouldBoot()) {
            bootList.addAll(game.getPlayerUsernames());
            game = new Game();
        }
        if(post && request.getHeader("Ajax") != null) {
            if(bootList.contains(username)) {
                session.invalidate();
                response.sendRedirect("index.jsp");
                bootList.remove(username);
                return;
            }
            response.setContentType("application/xml;charset=UTF-8");
            PrintWriter out = response.getWriter();
            try {
                out.println("<?xml version='1.0' encoding='UTF-8'?>");
                if(request.getHeader("Ajax").equals("get")) {
                    String resource = request.getHeader("resource");
                    if(resource.equals("game")) {
                        out.println("<game>");
                        GameBean bean = game.getBean((String)session.getAttribute("username"));
                        if(bean != null) {
                            out.println("<trump>" + bean.getTrump() + "</trump>");
                            xmlcards(out, "cards", bean.getCards());
                            xmlcards(out, "ontable", bean.getOnTable());
                            for(String player : bean.getPlayers()) {
                                out.println("<player>" + player + "</player>");
                            }
                            xmlcards(out, "trumpcard", Arrays.asList(bean.getTrumpCard()));
                            out.println("<alone>" + bean.isAlone() + "</alone>");
                            out.println("<teammate>" + bean.getTeammate() + "</teammate>");
                            out.println("<phase>" + bean.getPhase() + "</phase>");
                            out.println("<playerturn>" + bean.getPlayerTurn() + "</playerturn>");
                            out.println("<dealer>" + bean.getDealer() + "</dealer>");
                            out.println("<bidwinner>" + bean.getBidWinner() + "</bidwinner>");
                            out.println("<ourscore>" + bean.getOurScore() + "</ourscore>");
                            out.println("<theirscore>" + bean.getTheirScore() + "</theirscore>");
                            out.println("<ourtricks>" + bean.getOurTricks() + "</ourtricks>");
                            out.println("<theirtricks>" + bean.getTheirTricks() + "</theirtricks>");
                        } else {
                            session.invalidate();
                        }
                        out.println("</game>");
                    } else {
                        out.println("<" + resource + ">" + game.getResource(resource) + "</" + resource + ">");
                    }
                } else if(request.getHeader("Ajax").equals("set")) {
                    String action = request.getHeader("action");
                    String actiondata = request.getHeader("actiondata");
                    out.println("<success>" + game.tryAction(username, action, actiondata) + "</success>");
                } else {
                    //?
                }
            } finally {            
                out.close();
            }
        } else if(post && request.getParameter("quit") != null) {
            game.playerQuit(username);
            session.invalidate();
            response.sendRedirect("index.jsp");
        } else {
            if(post && (username == null || username.equals(""))) {
                username = request.getParameter("username");
                String password = request.getParameter("password");
                if(username != null && !username.equals("")) {
                    if(!game.tryAddPlayer(username, password)) {
                        if(game.full())
                            session.setAttribute("full", "full");
                        if(game.getPlayerUsernames().contains(username))
                            session.setAttribute("invalidUsername", username);
                        username = null;
                    } else {
                        if(Game.debug && !game.full()) {
                            game.tryAddPlayer("DebugLeft", "debug");
                            game.tryAddPlayer("DebugPartner", "debug");
                            game.tryAddPlayer("DebugRight", "debug");
                            game.tryAction("DebugLeft", "request teammate", "DebugRight");
                            game.tryAction("DebugRight", "request teammate", "DebugLeft");
                            game.tryAction("DebugLeft", "begin", "");
                            game.tryAction("DebugPartner", "begin", "");
                            game.tryAction("DebugRight", "begin", "");
                        }
                        session.setAttribute("username", username);
                    }
                }
            }
            
            if(username != null && !username.equals("")) {
                request.setAttribute("game", game.getBean(username));
                response.sendRedirect("table.jsp");
                //RequestDispatcher dispatcher = request.getRequestDispatcher("/table.jsp");
                //dispatcher.forward(request, response);
            } else {
                response.sendRedirect("index.jsp");
                //RequestDispatcher dispatcher = request.getRequestDispatcher("/index.jsp");
                //dispatcher.forward(request, response);
            }
        }
    }

    /**
     * Handles the HTTP
     * <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response, false);
    }

    /**
     * Handles the HTTP
     * <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response, true);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }
}
