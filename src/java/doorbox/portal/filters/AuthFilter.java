package doorbox.portal.filters;

import java.io.IOException;
import javax.faces.application.ViewExpiredException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebFilter(filterName = "AuthFilter", urlPatterns = { "*.xhtml" })
public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    /*@Override
    public void doFilter(ServletRequest req, ServletResponse res,
            FilterChain chain) throws IOException, ServletException {
        try {
            //System.out.println("doFilter ...");
            HttpServletRequest request = (HttpServletRequest) req;
            HttpServletResponse response = (HttpServletResponse) res;
            HttpSession session = request.getSession(false);
            String email = (session != null) ? (String)session.getAttribute("email") : null;
            String myContextURL = request.getContextPath() + "/my";
            boolean myAccountRequest = request.getRequestURI().startsWith(myContextURL);
            //System.out.println("RequestURI: " + request.getRequestURI());
            if (email == null && myAccountRequest) {
                //System.out.println("/my*.xhtml request but user not logged in. Redirecting to login");
                response.sendRedirect(request.getContextPath() + "/login.xhtml");
            } else {
                //System.out.println("Let filter pass request");
                chain.doFilter(request, response);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }*/
    
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        //System.out.println("doFilter ...");
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        
        try {
            HttpSession session = request.getSession(false);
            String path = request.getRequestURI().substring(request.getContextPath().length());

            //System.out.println("filtered path: " + path);
            
            String role = "unknown";
            if (session != null && session.getAttribute("role") != null) {
                role = (String)session.getAttribute("role");
            }

            if (role.equals("admin")) {
                chain.doFilter(request, response);
                return;
            }
                
            if (path.startsWith("/admin")) {
                response.sendRedirect(request.getContextPath() + "/login.xhtml");
                return;
            }
            
            if (path.startsWith("/my") && !role.equals("subscriber")) {
                response.sendRedirect(request.getContextPath() + "/login.xhtml");
                return;
            }
            
            chain.doFilter(request, response);

        } catch (ViewExpiredException e) {
            System.out.println("View expired. Refreshing the login page" + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/login.xhtml");
        } catch (Exception e) {
            //System.out.println("An error occured in doFilter: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void destroy() {
        //      
    }
}
