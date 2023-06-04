import entities.Book;
import entities.Borrow;
import entities.Card;
import queries.*;
import queries.BorrowHistories.Item;
import utils.DBInitializer;
import utils.DatabaseConnector;

import java.sql.*;
import java.util.*;

public class LibraryManagementSystemImpl implements LibraryManagementSystem {

    private final DatabaseConnector connector;

    public LibraryManagementSystemImpl(DatabaseConnector connector) {
        this.connector = connector;
    }

    @Override
    public ApiResult storeBook(Book book) {
        Connection conn = connector.getConn();
        try{
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            String select_sql="select * from library.book where category=? and title=? and press=? and publish_year=? and author=?";
            PreparedStatement pstmt=conn.prepareStatement(select_sql);
            pstmt.setString(1, book.getCategory());
            pstmt.setString(2, book.getTitle());
            pstmt.setString(3, book.getPress());
            pstmt.setInt(4, book.getPublishYear());
            pstmt.setString(5, book.getAuthor());
            ResultSet rs=pstmt.executeQuery();
            if(!rs.next()){
                String insert_query="insert into library.book values(NULL,?,?,?,?,?,?,?)";
                pstmt=conn.prepareStatement(insert_query,Statement.RETURN_GENERATED_KEYS);
                pstmt.setString(1, book.getCategory());
                pstmt.setString(2, book.getTitle());
                pstmt.setString(3, book.getPress());
                pstmt.setInt(4, book.getPublishYear());
                pstmt.setString(5, book.getAuthor());
                pstmt.setDouble(6, book.getPrice());
                pstmt.setInt(7, book.getStock());
                pstmt.executeUpdate();
                ResultSet insert_rs=pstmt.getGeneratedKeys();
                insert_rs.next();
                book.setBookId(insert_rs.getInt(1));
                conn.commit();
                return new ApiResult(true, "Success store book");
            }
            else{
                return new ApiResult(false, "Exist this book, fail to store book");
            }
        }catch (Exception e) {
            rollback(conn);
            return new ApiResult(false, e.getMessage());
        }
    }

    @Override
    public ApiResult incBookStock(int bookId, int deltaStock) {
        Connection conn = connector.getConn();
        try {
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "select stock from library.book where book_id=" + bookId
            );
            if(rs.next()) {
                int stock = rs.getInt(("stock"));
                if (stock + deltaStock < 0) {
                    return new ApiResult(false, "Invalid operation: the resulting stock would be negative");
                }
                int newStock = stock + deltaStock;
                stmt.executeUpdate(
                        "update library.book set stock=" + newStock + " where book_id=" + bookId
                );
                conn.commit();
                return new ApiResult(true, "Success incBookStock");
            } else {
                return new ApiResult(false, "Cannot find book with bookId=" + bookId);
            }

        } catch (Exception e) {
            rollback(conn);
            return new ApiResult(false, e.getMessage());
        }
    }

    @Override
    public ApiResult storeBook(List<Book> books) {
        Connection conn = connector.getConn();
        try {
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            PreparedStatement stmt = conn.prepareStatement(
                    "select * " +
                            " from library.book " +
                            " where category=? " +
                            " and title=? " +
                            " and press=? " +
                            " and publish_year=? " +
                            " and author=?"
            );

            PreparedStatement insertStmt = conn.prepareStatement(
                    "insert into library.book " +
                            "(category, title, press, publish_year, author, price, stock) " +
                            "values (?, ?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );

            conn.setAutoCommit(false);

            for (Book book : books) {
                stmt.setString(1, book.getCategory());
                stmt.setString(2, book.getTitle());
                stmt.setString(3, book.getPress());
                stmt.setInt(4, book.getPublishYear());
                stmt.setString(5, book.getAuthor());

                ResultSet rs = stmt.executeQuery();
                if (!rs.next()) {
                    insertStmt.setString(1, book.getCategory());
                    insertStmt.setString(2, book.getTitle());
                    insertStmt.setString(3, book.getPress());
                    insertStmt.setInt(4, book.getPublishYear());
                    insertStmt.setString(5, book.getAuthor());
                    insertStmt.setDouble(6, book.getPrice());
                    insertStmt.setInt(7, book.getStock());

                    insertStmt.executeUpdate();
                    ResultSet generatedKeys = insertStmt.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        int newId = generatedKeys.getInt(1);
                        book.setBookId(newId);
                    }
                } else {
                    conn.rollback();
                    return new ApiResult(false, "Exist thisBook, fail to storeBook");
                }
            }

            conn.commit();

            return new ApiResult(true, "Success storeBook");
        } catch (Exception e) {
            rollback(conn);
            return new ApiResult(false, e.getMessage());
        }
    }

    @Override
    public ApiResult removeBook(int bookId) {
        Connection conn = connector.getConn();
        try {
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "select * " +
                            " from library.borrow " +
                            " where book_id=" + bookId +
                            " and return_time=0"
            );
            if(rs.next()) return new ApiResult(false, "Someone has not returned this book, fail to removeBook");

//            remove book
            PreparedStatement pstmt = conn.prepareStatement(
                    "delete from library.book " +
                            " where book_id=?"
            );
            pstmt.setInt(1, bookId);
            int rows = pstmt.executeUpdate();
            conn.commit();
            if(rows != 1) return new ApiResult(false, "Failed removeBook");
            return new ApiResult(true, "Success removeBook");
        } catch (Exception e) {
            rollback(conn);
            return new ApiResult(false, e.getMessage());
        }
    }

    @Override
    public ApiResult modifyBookInfo(Book book) {
        Connection conn = connector.getConn();
        try {
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            PreparedStatement pstmt = conn.prepareStatement(
                    "update library.book set category=?, title=?, press=?, publish_year=?, author=?, price=? where book_id=?"
            );
            pstmt.setString(1, book.getCategory());
            pstmt.setString(2, book.getTitle());
            pstmt.setString(3, book.getPress());
            pstmt.setInt(4, book.getPublishYear());
            pstmt.setString(5, book.getAuthor());
            pstmt.setDouble(6, book.getPrice());
            pstmt.setInt(7, book.getBookId());
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected == 0) {
                return new ApiResult(false, "Fail to modifyBookInfo");
            } else {
                conn.commit();
                return new ApiResult(true, "Success modifyBookInfo");
            }
        } catch (Exception e) {
            rollback(conn);
            return new ApiResult(false, e.getMessage());
        }
    }

    @Override
    public ApiResult queryBook(BookQueryConditions conditions) {
        Connection conn = connector.getConn();
        try {
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            // build query SQL statement
            StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM book WHERE 1=1 ");
//            List<Object> params = new ArrayList<>();
            if (conditions.getCategory() != null) {
                sqlBuilder.append(" AND category = '");
                sqlBuilder.append(conditions.getCategory()).append("'");
            }
            if (conditions.getTitle() != null) {
                sqlBuilder.append(" AND title like ");
                sqlBuilder.append("'%").append(conditions.getTitle()).append("%'");
            }
            if (conditions.getPress() != null) {
                sqlBuilder.append(" AND press like ");
                sqlBuilder.append("'%").append(conditions.getPress()).append("%'");
            }
            if (conditions.getMinPublishYear() != null) {
                sqlBuilder.append(" AND publish_year >= ");
                sqlBuilder.append(conditions.getMinPublishYear());
            }
            if (conditions.getMaxPublishYear() != null) {
                sqlBuilder.append(" AND publish_year <= ");
                sqlBuilder.append(conditions.getMaxPublishYear());
            }
            if (conditions.getMinPrice() != null) {
                sqlBuilder.append(" AND price >= ");
                sqlBuilder.append(conditions.getMinPrice());
            }
            if (conditions.getMaxPrice() != null) {
                sqlBuilder.append(" AND price <= ");
                sqlBuilder.append(conditions.getMaxPrice());
            }
            if (conditions.getAuthor() != null) {
                sqlBuilder.append(" AND author like ");
                sqlBuilder.append("'%").append(conditions.getAuthor()).append("%'");
            }

            // sort by book_id in ascending order if all else is equal
            sqlBuilder.append(" ORDER BY ").append(conditions.getSortBy().getValue()).append(" ")
                    .append(conditions.getSortOrder().getValue());
            if(!Objects.equals(conditions.getSortBy().getValue(), "book_id"))
                sqlBuilder.append(",book_id");

            PreparedStatement stmt = conn.prepareStatement(sqlBuilder.toString());

            ResultSet rs = stmt.executeQuery();
            List<Book> books = new ArrayList<>();
            while (rs.next()) {
                Book book = new Book();
                book.setBookId(rs.getInt("book_id"));
                book.setCategory(rs.getString("category"));
                book.setTitle(rs.getString("title"));
                book.setPress(rs.getString("press"));
                book.setPublishYear(rs.getInt("publish_year"));
                book.setAuthor(rs.getString("author"));
                book.setPrice(rs.getDouble("price"));
                book.setStock(rs.getInt("stock"));
                books.add(book);
            }
            BookQueryResults results = new BookQueryResults(books);
            conn.commit();
            return new ApiResult(true, "Success queryBook", results);
        } catch (Exception e) {
            return new ApiResult(false, e.getMessage());
        }
    }

    @Override
    public ApiResult borrowBook(Borrow borrow) {
        Connection conn = connector.getConn();
        try {
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            // check if the book exists in the library system
            PreparedStatement pstmt = conn.prepareStatement(
                    "select * from library.book where book_id = ?"
            );
            pstmt.setInt(1, borrow.getBookId());
            ResultSet rs = pstmt.executeQuery();
            if(!rs.next()) {
                return new ApiResult(false, "This book does not exist in the library system.");
            }
            int nowStock = rs.getInt("stock");
            if(nowStock <= 0) return new ApiResult(false, "This book does not exist in the library system.");
            pstmt = conn.prepareStatement(
                    "select * from library.card where card_id = ?"
            );
            pstmt.setInt(1, borrow.getCardId());
            rs = pstmt.executeQuery();
            if(!rs.next()) {
                return new ApiResult(false, "This card does not exist in the library system.");
            }
            // check if the user has borrowed this book or has returned it
            pstmt = conn.prepareStatement(
                    "select * from library.borrow where card_id = ? and book_id = ? and return_time=0"
            );
            pstmt.setInt(1, borrow.getCardId());
            pstmt.setInt(2, borrow.getBookId());
            rs = pstmt.executeQuery();
            if(rs.next()) return new ApiResult(false, "This user borrowed this book");

//            borrow the book
            conn.setAutoCommit(false);
            pstmt = conn.prepareStatement(
                    "insert into library.borrow (card_id, book_id, borrow_time, return_time) values (?, ?, ?, 0)"
            );
            pstmt.setInt(1, borrow.getCardId());
            pstmt.setInt(2, borrow.getBookId());
            pstmt.setLong(3, borrow.getBorrowTime());
            int rows = pstmt.executeUpdate();
            if(rows != 1) {
                rollback(conn);
                return new ApiResult(false, "Failed to select this book.");
            }
            ApiResult updateResult = incBookStock(borrow.getBookId(), -1);
            if(updateResult.ok) {
                conn.commit();
                return  new ApiResult(true, "Success to borrow this book");
            } else {
                return new ApiResult(false, "Failed to borrow this book.");
            }
        } catch (Exception e) {
            rollback(conn);
            return new ApiResult(false, e.getMessage());
        }
    }

    @Override
    public ApiResult returnBook(Borrow borrow) {
        Connection conn = connector.getConn();
        try {
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            PreparedStatement pstmt = conn.prepareStatement(
                    "select * from library.book where book_id = ?"
            );
            pstmt.setInt(1, borrow.getBookId());
            ResultSet rs = pstmt.executeQuery();
            if(!rs.next()) {
                return new ApiResult(false, "This book does not exist in the library system.");
            }
            pstmt = conn.prepareStatement(
                    "select * from library.card where card_id = ?"
            );
            pstmt.setInt(1, borrow.getCardId());
            rs = pstmt.executeQuery();
            if(!rs.next()) {
                return new ApiResult(false, "This card does not exist in the library system.");
            }
            pstmt = conn.prepareStatement(
                    "select * from library.borrow where card_id = ? and book_id = ? and return_time=0"
            );
            pstmt.setInt(1, borrow.getCardId());
            pstmt.setInt(2, borrow.getBookId());
            rs = pstmt.executeQuery();
            if(!rs.next()) return new ApiResult(false, "This user returned this book");
            pstmt = conn.prepareStatement(
                    "update library.borrow set return_time=? where card_id=? and book_id=? and borrow_time=?"
            );
            pstmt.setLong(1, borrow.getReturnTime());
            pstmt.setInt(2, borrow.getCardId());
            pstmt.setInt(3, borrow.getBookId());
            pstmt.setLong(4, borrow.getBorrowTime());
            int rows = pstmt.executeUpdate();
            if(rows != 1) return new ApiResult(false, "Failed to return");
            ApiResult updateResult = incBookStock(borrow.getBookId(), 1);
            if(updateResult.ok) {
                conn.commit();
                return  new ApiResult(true, "Success return the book");
            }
            else {
                rollback(conn);
                return updateResult;
            }
        } catch (Exception e) {
            rollback(conn);
            return new ApiResult(false, e.getMessage());
        }
    }

    @Override
    public ApiResult showBorrowHistory(int cardId) {
        Connection conn = connector.getConn();
        try{
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            PreparedStatement pstmt = conn.prepareStatement(
                    "select * from library.borrow natural join library.book where" +
                            " card_id = ?" +
                            " order by borrow_time desc"
            );
            pstmt.setInt(1, cardId);
            ResultSet rs=pstmt.executeQuery();
            List<Item> items = new ArrayList<>();
            while(rs.next()){
                Book book = new Book(
                        rs.getString("category"),
                        rs.getString("title"),
                        rs.getString("press"),
                        rs.getInt("publish_year"),
                        rs.getString("author"),
                        rs.getDouble("price"),
                        rs.getInt("stock")
                );
                book.setBookId(rs.getInt("book_id"));
                Borrow borrow = new Borrow();
                borrow.setBorrowTime(rs.getLong("borrow_time"));
                borrow.setReturnTime(rs.getLong("return_time"));
                items.add(new Item(cardId,book,borrow));
            }
            conn.commit();
            return new ApiResult(true,"Success show borrow history ",new BorrowHistories(items));
        }catch(Exception e){
            rollback(conn);
            return new ApiResult(false, e.getMessage());
        }
    }

    @Override
    public ApiResult registerCard(Card card) {
        Connection conn = connector.getConn();
        try {
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            PreparedStatement pstmt = conn.prepareStatement("select * from library.card where name=? and department=? and type=?");
            pstmt.setString(1, card.getName());
            pstmt.setString(2, card.getDepartment());
            pstmt.setString(3, card.getType().getStr());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return new ApiResult(false, "Card already exists");

            String sql = "insert into library.card values(NULL,?,?,?)";
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, card.getName());
            pstmt.setString(2, card.getDepartment());
            pstmt.setString(3, card.getType().getStr());
            int rows = pstmt.executeUpdate();
            if(rows > 0) {
                rs = pstmt.getGeneratedKeys();
                rs.next();
                card.setCardId(rs.getInt(1));
                conn.commit();
                return new ApiResult(true, "Success to register card");
            } else {
                rollback(conn);
                return new ApiResult(false, "Failed to register card");
            }

        } catch (Exception e) {
            rollback(conn);
            return new ApiResult(false, e.getMessage());
        }
    }

    @Override
    public ApiResult removeCard(int cardId) {
        Connection conn = connector.getConn();
        try {
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            Statement stmt = conn.createStatement();
            ResultSet rs=stmt.executeQuery(
                    "select * from library.borrow where card_id=" + cardId +
                            " and return_time=0"
            );
            if(rs.next()) return new ApiResult(false, "There are unreturned books under this user, the card cannot be removed");
            PreparedStatement pstmt = conn.prepareStatement(
                    "delete from library.card where card_id=?"
            );
            pstmt.setInt(1, cardId);
            int rows = pstmt.executeUpdate();
            if (rows == 0) {
                return new ApiResult(false, "The card does not exist");
            }
            conn.commit();
            return new ApiResult(true, "Success to remove card");
        }catch(Exception e){
            rollback(conn);
            return new ApiResult(false, e.getMessage());
        }
    }

    @Override
    public ApiResult showCards() {
        Connection conn = connector.getConn();
        try {
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            PreparedStatement pstmt = conn.prepareStatement("select * from library.card order by card_id");
            ResultSet rs = pstmt.executeQuery();
            List<Card> cards = new ArrayList<>();
            while(rs.next()){
                Card card =new Card();
                card.setCardId(rs.getInt(1));
                card.setName(rs.getString(2));
                card.setDepartment(rs.getString(3));
                card.setType(Card.CardType.values(rs.getString(4)));
                cards.add(card);
            }
            conn.commit();
            return new ApiResult(true,"Success to show card",new CardList(cards));
        }catch(Exception e){
            rollback(conn);
            return new ApiResult(false, e.getMessage());
        }
    }

    @Override
    public ApiResult resetDatabase() {
        Connection conn = connector.getConn();
        try {
            Statement stmt = conn.createStatement();
            DBInitializer initializer = connector.getConf().getType().getDbInitializer();
            stmt.addBatch(initializer.sqlDropBorrow());
            stmt.addBatch(initializer.sqlDropBook());
            stmt.addBatch(initializer.sqlDropCard());
            stmt.addBatch(initializer.sqlCreateCard());
            stmt.addBatch(initializer.sqlCreateBook());
            stmt.addBatch(initializer.sqlCreateBorrow());
            stmt.executeBatch();
            commit(conn);
        } catch (Exception e) {
            rollback(conn);
            return new ApiResult(false, e.getMessage());
        }
        return new ApiResult(true, null);
    }

    private void rollback(Connection conn) {
        try {
            conn.rollback();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void commit(Connection conn) {
        try {
            conn.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 下面是我自己的函数
     */
    public ApiResult showBooks() {
        Connection conn = connector.getConn();
        try {
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            PreparedStatement pstmt = conn.prepareStatement(" select * from library.book order by book_id ");
            ResultSet rs = pstmt.executeQuery();
            List<Book> books = new ArrayList<>();
            while(rs.next()){
                Book book =new Book();
                book.setBookId(rs.getInt(1));
                book.setCategory(rs.getString(2));
                book.setTitle(rs.getString(3));
                book.setPress(rs.getString(4));
                book.setPublishYear(rs.getInt(5));
                book.setAuthor(rs.getString(6));
                book.setPrice(rs.getInt(7));
                book.setStock(rs.getInt(8));
                books.add(book);
            }
            conn.commit();
            return new ApiResult(true,"Success to show book",new BookQueryResults(books));
        }catch(Exception e){
            rollback(conn);
            return new ApiResult(false, e.getMessage());
        }
    }

    public ApiResult showBorrows() {
        Connection conn = connector.getConn();
        try{
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            PreparedStatement pstmt = conn.prepareStatement(
                    "select * from library.borrow natural join library.book natural join library.card" +
                            " order by borrow_time desc"
            );
            ResultSet rs=pstmt.executeQuery();
            List<Item> items = new ArrayList<>();
            while(rs.next()){
                Book book = new Book(
                        rs.getString("category"),
                        rs.getString("title"),
                        rs.getString("press"),
                        rs.getInt("publish_year"),
                        rs.getString("author"),
                        rs.getDouble("price"),
                        rs.getInt("stock")
                );
                book.setBookId(rs.getInt("book_id"));
                Borrow borrow = new Borrow();
                borrow.setBorrowTime(rs.getLong("borrow_time"));
                borrow.setReturnTime(rs.getLong("return_time"));
                int cardId = rs.getInt("card_id");;
                items.add(new Item(cardId,book,borrow));
            }
            conn.commit();
            return new ApiResult(true,"Success show borrows ",new BorrowHistories(items));
        }catch(Exception e){
            rollback(conn);
            return new ApiResult(false, e.getMessage());
        }
    }
}
