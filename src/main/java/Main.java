import queries.*;

import queries.SortOrder;
import utils.*;

import entities.Book;
import entities.Borrow;
import entities.Card;
import entities.Book.SortColumn;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.logging.Logger;

public class Main {

    private static final Logger log = Logger.getLogger(Main.class.getName());
    private DatabaseConnector connector;
    private LibraryManagementSystem library;
    JFrame f = new JFrame("Library Manage System");
    JButton reset, add, incBook, adds, removeBook, modify, query,
            borrow, return_book, queryBorrow, register, remove;
    JScrollPane scrollPane_book, scrollPane_borrow, scrollPane_card;
    private static long getTime()
    {
        long timestamp = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return Long.parseLong(sdf.format(new Date(timestamp)));
    }


    public void reset() {
        reset = new JButton("重置数据库");
        reset.addActionListener(e -> {
            int response = JOptionPane.showConfirmDialog(null, "您确定要重置数据库吗？", "操作确认", JOptionPane.YES_NO_OPTION);
            if(response==0){
                ApiResult result = library.resetDatabase();
                String res_title;
                if (!result.ok) {
                    res_title = "Failed to reset";
                    System.out.printf("Failed to reset database, reason: %s\n", result.message);
                } else {
                    res_title = "Success to reset";
                    if(scrollPane_borrow != null) scrollPane_borrow.setVisible(false);
                    if(scrollPane_card != null) scrollPane_card.setVisible(false);
                    if(scrollPane_book != null) scrollPane_book.setVisible(false);
                    scrollPane_book = null;
                    scrollPane_borrow = null;
                    scrollPane_card = null;
                    System.out.print("Success to reset database\n");
                }
                JFrame res_submit = new JFrame(res_title);
                res_submit.setSize(350,300);
                res_submit.setLayout(null);
                res_submit.setVisible(true);
                JLabel res_label=new JLabel();
                res_label.setText(res_title);
                res_label.setFont(new Font(res_label.getFont().getName(),res_label.getFont().getStyle(),15));
                res_label.setBounds(0,100,300,30);
                res_label.setHorizontalAlignment(JLabel.CENTER);
                res_submit.add(res_label);
                JButton Back=new JButton("Back");
                Back.setBounds(100,200,100,30);
                res_submit.add(Back);
                Back.addActionListener(e1 -> res_submit.setVisible(false));
            }
        });
        reset.setFocusPainted(false);
        reset.setBackground(Color.red);
        reset.setBounds(800,600,150,50);
        f.add(reset);
    }

    public void judge() {
        if(scrollPane_borrow != null) scrollPane_borrow.setVisible(false);
        if(scrollPane_card != null) scrollPane_card.setVisible(false);
        if(scrollPane_book != null) scrollPane_book.setVisible(false);
        scrollPane_book = null;
        scrollPane_borrow = null;
        scrollPane_card = null;
    }

    public void storeBook() {
        add=new JButton("添加书籍");
        add.addActionListener(e -> {
            JFrame addf=new JFrame("添加书籍");
//                    设置label
            JLabel cat = new JLabel("category:");
            JLabel tit = new JLabel("title:");
            JLabel pre = new JLabel("press:");
            JLabel pub = new JLabel("publishYear:");
            JLabel aut = new JLabel("author:");
            JLabel pri = new JLabel("price:");
            JLabel sto = new JLabel("stock:");
//                    设置大小
            cat.setBounds(10,50,90,30);
            tit.setBounds(10,85,90,30);
            pre.setBounds(10,120,90,30);
            pub.setBounds(10,155,90,30);
            aut.setBounds(10,190,90,30);
            pri.setBounds(10,225,90,30);
            sto.setBounds(10,260,90,30);
//                    加进frame
            addf.add(cat);
            addf.add(tit);
            addf.add(pre);
            addf.add(pub);
            addf.add(aut);
            addf.add(pri);
            addf.add(sto);
//                    文本框
            JTextField category=new JTextField();
            JTextField title=new JTextField();
            JTextField press=new JTextField();
            JTextField publishYear=new JTextField();
            JTextField author=new JTextField();
            JTextField price=new JTextField();
            JTextField stock=new JTextField();
            category.setBounds(100,50,150,30);
            title.setBounds(100,85,150,30);
            press.setBounds(100,120,150,30);
            publishYear.setBounds(100,155,150,30);
            author.setBounds(100,190,150,30);
            price.setBounds(100,225,150,30);
            stock.setBounds(100,260,150,30);
            addf.add(category);
            addf.add(title);
            addf.add(press);
            addf.add(publishYear);
            addf.add(author);
            addf.add(price);
            addf.add(stock);
//                    提交
            JButton submit = new JButton("提交");
            submit.setBounds(50,295,150,30);
            addf.add(submit);
            submit.addActionListener(e12 -> {
                addf.setVisible(false);
                Book store_book = new Book(
                        category.getText(),
                        title.getText(),
                        press.getText(),
                        Integer.parseInt(publishYear.getText()),
                        author.getText(),
                        Double.parseDouble(price.getText()),
                        Integer.parseInt(stock.getText())
                );
                ApiResult res = library.storeBook(store_book);
                String res_title;
                if(res.ok) {
                    res_title = "Success store";
                    System.out.print("Success to store book\n");
                }
                else {
                    res_title = "Failed to store";
                    System.out.print("Failed to store book\n");
                }
                JFrame res_submit = new JFrame(res_title);
                res_submit.setSize(350,300);
                res_submit.setLayout(null);
                res_submit.setVisible(true);
                JLabel res_label=new JLabel();
                res_label.setText(res_title);
                res_label.setFont(new Font(res_label.getFont().getName(),res_label.getFont().getStyle(),15));
                res_label.setBounds(0,100,300,30);
                res_label.setHorizontalAlignment(JLabel.CENTER);
                res_submit.add(res_label);
                JButton Back=new JButton("Back");
                Back.setBounds(100,200,100,30);
                res_submit.add(Back);
                judge();
                showBooks();
                Back.addActionListener(e1 -> res_submit.setVisible(false));
            });
            addf.setSize(300,500);
            addf.setLayout(null);
            addf.setVisible(true);
        });
        add.setVisible(false);
        add.setBounds(200,500,150,50);
        f.add(add);
    }

    public void incBookStock() {
        incBook=new JButton("修改库存");
        incBook.addActionListener(e -> {
            JFrame incf=new JFrame("修改库存");
//                    设置label
            JLabel ID = new JLabel("bookID:");
            JLabel delta = new JLabel("deltaStock:");
//                    设置大小
            ID.setBounds(10,50,90,30);
            delta.setBounds(10,85,90,30);
//                    加进frame
            incf.add(ID);
            incf.add(delta);
//                    文本框
            JTextField bookID=new JTextField();
            JTextField deltaStock=new JTextField();
            bookID.setBounds(100,50,150,30);
            deltaStock.setBounds(100,85,150,30);
            incf.add(bookID);
            incf.add(deltaStock);
//                    提交
            JButton submit = new JButton("提交");
            submit.setBounds(50,295,150,30);
            incf.add(submit);
            submit.addActionListener(e12 -> {
                incf.setVisible(false);
                ApiResult res = library.incBookStock(
                        Integer.parseInt(bookID.getText()),
                        Integer.parseInt(deltaStock.getText())
                );
                String res_title;
                if(res.ok) {
                    res_title = "Success modify the stock";
                    System.out.print("Success modify the stock\n");
                }
                else {
                    res_title = "Failed to modify the stock";
                    System.out.print("Failed to modify the stock\n");
                }
                JFrame res_submit = new JFrame(res_title);
                res_submit.setSize(350,300);
                res_submit.setLayout(null);
                res_submit.setVisible(true);
                JLabel res_label=new JLabel();
                res_label.setText(res_title);
                res_label.setFont(new Font(res_label.getFont().getName(),res_label.getFont().getStyle(),15));
                res_label.setBounds(0,100,300,30);
                res_label.setHorizontalAlignment(JLabel.CENTER);
                res_submit.add(res_label);
                JButton Back=new JButton("Back");
                Back.setBounds(100,200,100,30);
                res_submit.add(Back);
                judge();
                showBooks();
                Back.addActionListener(e1 -> res_submit.setVisible(false));
            });
            incf.setSize(300,500);
            incf.setLayout(null);
            incf.setVisible(true);
        });
        incBook.setVisible(false);
        incBook.setBounds(400,500,150,50);
        f.add(incBook);
    }

    public void storeBookList() {
        adds=new JButton("批量添加书籍");
        adds.addActionListener(e -> {
            JFrame addsf=new JFrame("批量添加书籍: 请选择文件");
//                文件读取
            JButton book_dir = new JButton("读入文件");
            book_dir.setBounds(50,50,150,30);
            addsf.add(book_dir);
            List<Book> books = new ArrayList<>();
            book_dir.addActionListener(e14 -> {
                JFileChooser filechooser = new JFileChooser();
                FileNameExtensionFilter txtfilter=new FileNameExtensionFilter("txt files(*.txt)","txt");
                filechooser.setFileFilter(txtfilter);
                filechooser.setDialogTitle("打开txt文件");
                filechooser.showOpenDialog(addsf);
                String name = "已选择文件: " + filechooser.getSelectedFile().getName() + " 请提交";
                addsf.setTitle(name);

                String path = filechooser.getSelectedFile().getPath();
                try(Scanner scanner = new Scanner(new FileReader(path))) {
                    while(scanner.hasNextLine()) {
                        String line = scanner.nextLine();
                        List<String> data = new ArrayList<>(Arrays.asList(line.split(",")));
                        Book book = new Book(
                                data.get(0),
                                data.get(1),
                                data.get(2),
                                Integer.parseInt(data.get(3)),
                                data.get(4),
                                Double.parseDouble(data.get(5)),
                                Integer.parseInt(data.get(6))
                        );
                        books.add(book);
                    }
                } catch (FileNotFoundException fileNotFoundException) {
                    fileNotFoundException.printStackTrace();
                }

            });
//                提交
            JButton submit = new JButton("提交");
            submit.setBounds(50,295,150,30);
            addsf.add(submit);
            submit.addActionListener(e12 -> {
                addsf.setVisible(false);
                ApiResult res = library.storeBook(books);
                String res_title;
                if(res.ok) {
                    res_title = "Success store books";
                    System.out.print("Success to store books\n");
                }
                else {
                    res_title = "Failed to store books";
                    System.out.print("Failed to store books\n");
                }
                JFrame res_submit = new JFrame(res_title);
                res_submit.setSize(350,300);
                res_submit.setLayout(null);
                res_submit.setVisible(true);

                JLabel res_label=new JLabel();
                res_label.setText(res_title);
                res_label.setFont(new Font(res_label.getFont().getName(),res_label.getFont().getStyle(),15));
                res_label.setBounds(0,100,300,30);
                res_label.setHorizontalAlignment(JLabel.CENTER);
                res_submit.add(res_label);
                JButton Back=new JButton("Back");
                Back.setBounds(100,200,100,30);
                res_submit.add(Back);
                judge();
                showBooks();
                Back.addActionListener(e1 -> res_submit.setVisible(false));
            });
            addsf.setSize(400,500);
            addsf.setLayout(null);
            addsf.setVisible(true);
        });
        adds.setVisible(false);
        adds.setBounds(600,500,150,50);
        f.add(adds);
    }

    public void removeBook() {
        removeBook=new JButton("删除图书");
        removeBook.addActionListener(e -> {
            JFrame remf=new JFrame("删除图书");
//                    设置label
            JLabel ID = new JLabel("bookID:");
//                    设置大小
            ID.setBounds(10,50,90,30);
//                    加进frame
            remf.add(ID);
//                    文本框
            JTextField bookID=new JTextField();
            bookID.setBounds(100,50,150,30);
            remf.add(bookID);
//                    提交
            JButton submit = new JButton("提交");
            submit.setBounds(50,295,150,30);
            remf.add(submit);
            submit.addActionListener(e12 -> {
                remf.setVisible(false);
                ApiResult res = library.removeBook(
                        Integer.parseInt(bookID.getText())
                );
                String res_title;
                if(res.ok) {
                    res_title = "Success remove book";
                    System.out.print("Success remove book\n");
                }
                else {
                    res_title = "Failed to remove book";
                    System.out.print("Failed to remove book\n");
                }
                JFrame res_submit = new JFrame(res_title);
                res_submit.setSize(350,300);
                res_submit.setLayout(null);
                res_submit.setVisible(true);
                JLabel res_label=new JLabel();
                res_label.setText(res_title);
                res_label.setFont(new Font(res_label.getFont().getName(),res_label.getFont().getStyle(),15));
                res_label.setBounds(0,100,300,30);
                res_label.setHorizontalAlignment(JLabel.CENTER);
                res_submit.add(res_label);
                JButton Back=new JButton("Back");
                Back.setBounds(100,200,100,30);
                res_submit.add(Back);
                judge();
                showBooks();
                Back.addActionListener(e1 -> res_submit.setVisible(false));
            });
            remf.setSize(300,500);
            remf.setLayout(null);
            remf.setVisible(true);
        });
        removeBook.setVisible(false);
        removeBook.setBounds(200,575,150,50);
        f.add(removeBook);
    }

    public void modifyBook() {
        modify=new JButton("修改书籍");
        modify.addActionListener(e -> {
            JFrame modifyf=new JFrame("修改书籍");
//                    设置label
            JLabel ID = new JLabel("bookID:");
            JLabel cat = new JLabel("category:");
            JLabel tit = new JLabel("title:");
            JLabel pre = new JLabel("press:");
            JLabel pub = new JLabel("publishYear:");
            JLabel aut = new JLabel("author:");
            JLabel pri = new JLabel("price:");
            JLabel sto = new JLabel("stock:");
//                    设置大小
            ID.setBounds(10,15,90,30);
            cat.setBounds(10,50,90,30);
            tit.setBounds(10,85,90,30);
            pre.setBounds(10,120,90,30);
            pub.setBounds(10,155,90,30);
            aut.setBounds(10,190,90,30);
            pri.setBounds(10,225,90,30);
            sto.setBounds(10,260,90,30);
//                    加进frame
            modifyf.add(ID);
            modifyf.add(cat);
            modifyf.add(tit);
            modifyf.add(pre);
            modifyf.add(pub);
            modifyf.add(aut);
            modifyf.add(pri);
            modifyf.add(sto);
//                    文本框
            JTextField bookID=new JTextField();
            JTextField category=new JTextField();
            JTextField title=new JTextField();
            JTextField press=new JTextField();
            JTextField publishYear=new JTextField();
            JTextField author=new JTextField();
            JTextField price=new JTextField();
            JTextField stock=new JTextField();
            bookID.setBounds(100,15,150,30);
            category.setBounds(100,50,150,30);
            title.setBounds(100,85,150,30);
            press.setBounds(100,120,150,30);
            publishYear.setBounds(100,155,150,30);
            author.setBounds(100,190,150,30);
            price.setBounds(100,225,150,30);
            stock.setBounds(100,260,150,30);
            modifyf.add(bookID);
            modifyf.add(category);
            modifyf.add(title);
            modifyf.add(press);
            modifyf.add(publishYear);
            modifyf.add(author);
            modifyf.add(price);
            modifyf.add(stock);
//                    提交
            JButton submit = new JButton("提交");
            submit.setBounds(50,295,150,30);
            modifyf.add(submit);
            submit.addActionListener(e12 -> {
                modifyf.setVisible(false);
                Book modify_book = new Book(
                        category.getText(),
                        title.getText(),
                        press.getText(),
                        Integer.parseInt(publishYear.getText()),
                        author.getText(),
                        Double.parseDouble(price.getText()),
                        Integer.parseInt(stock.getText())
                );
                modify_book.setBookId(Integer.parseInt(bookID.getText()));
                ApiResult res = library.modifyBookInfo(modify_book);
                String res_title;
                if(res.ok) {
                    res_title = "Success modify";
                    System.out.print("Success to modify book\n");
                }
                else {
                    res_title = "Failed to modify";
                    System.out.print("Failed to modify book\n");
                }
                JFrame res_submit = new JFrame(res_title);
                res_submit.setSize(350,300);
                res_submit.setLayout(null);
                res_submit.setVisible(true);
                JLabel res_label=new JLabel();
                res_label.setText(res_title);
                res_label.setFont(new Font(res_label.getFont().getName(),res_label.getFont().getStyle(),15));
                res_label.setBounds(0,100,300,30);
                res_label.setHorizontalAlignment(JLabel.CENTER);
                res_submit.add(res_label);
                JButton Back=new JButton("Back");
                Back.setBounds(100,200,100,30);
                res_submit.add(Back);
                judge();
                showBooks();
                Back.addActionListener(e1 -> res_submit.setVisible(false));
            });
            modifyf.setSize(300,500);
            modifyf.setLayout(null);
            modifyf.setVisible(true);
        });
        modify.setVisible(false);
        modify.setBounds(400,575,150,50);
        f.add(modify);
    }

    public void queryBook() {
        query=new JButton("查询书籍");
        query.addActionListener(e -> {
            JFrame queryf=new JFrame("查询书籍");
//                    设置label
            JLabel cat = new JLabel("category:");
            JLabel tit = new JLabel("title:");
            JLabel pre = new JLabel("press:");
            JLabel pub_min = new JLabel("MinPublishYear:");
            JLabel pub_max = new JLabel("MaxPublishYear:");
            JLabel aut = new JLabel("author:");
            JLabel pri_min = new JLabel("MinPrice:");
            JLabel pri_max = new JLabel("MaxPrice:");
            JLabel sor = new JLabel("Sort By:");
            JLabel sor_order = new JLabel("Sort Order:");
//                    设置大小
            cat.setBounds(10,15,90,30);
            tit.setBounds(10,50,90,30);
            pre.setBounds(10,85,90,30);
            pub_min.setBounds(10,120,90,30);
            pub_max.setBounds(10,155,90,30);
            aut.setBounds(10,190,90,30);
            pri_min.setBounds(10,225,90,30);
            pri_max.setBounds(10,260,90,30);
            sor.setBounds(10,295,90,30);
            sor_order.setBounds(10,330,90,30);
//                    加进frame
            queryf.add(cat);
            queryf.add(tit);
            queryf.add(pre);
            queryf.add(pub_min);
            queryf.add(pub_max);
            queryf.add(aut);
            queryf.add(pri_min);
            queryf.add(pri_max);
            queryf.add(sor);
            queryf.add(sor_order);
//                    文本框
            JTextField category=new JTextField();
            JTextField title=new JTextField();
            JTextField press=new JTextField();
            JTextField MinPublishYear=new JTextField();
            JTextField MaxPublishYear=new JTextField();
            JTextField author=new JTextField();
            JTextField MinPrice=new JTextField();
            JTextField MaxPrice=new JTextField();
            JComboBox<String>sor_text= new JComboBox<>();
            sor_text.addItem("BookId");
            sor_text.addItem("Category");
            sor_text.addItem("Title");
            sor_text.addItem("Press");
            sor_text.addItem("Publish year");
            sor_text.addItem("Author");
            sor_text.addItem("Price");
            sor_text.addItem("Stock");
            JComboBox<String>sor_order_text= new JComboBox<>();
            sor_order_text.addItem("asc");
            sor_order_text.addItem("desc");
            category.setBounds(100,15,150,30);
            title.setBounds(100,50,150,30);
            press.setBounds(100,85,150,30);
            MinPublishYear.setBounds(100,120,150,30);
            MaxPublishYear.setBounds(100,155,150,30);
            author.setBounds(100,190,150,30);
            MinPrice.setBounds(100,225,150,30);
            MaxPrice.setBounds(100,260,150,30);
            sor_text.setBounds(100,295,150,30);
            sor_order_text.setBounds(100,330,150,30);
            queryf.add(category);
            queryf.add(title);
            queryf.add(press);
            queryf.add(MinPublishYear);
            queryf.add(MaxPublishYear);
            queryf.add(author);
            queryf.add(MinPrice);
            queryf.add(MaxPrice);
            queryf.add(sor_text);
            queryf.add(sor_order_text);
//                    提交
            JButton submit = new JButton("提交");
            submit.setBounds(50,365,150,30);
            queryf.add(submit);
            submit.addActionListener(e12 -> {
                queryf.setVisible(false);
                BookQueryConditions conditions = new BookQueryConditions();
                if(category.getText().length()>0) conditions.setCategory(category.getText());
                if(title.getText().length()>0) conditions.setTitle(title.getText());
                if(press.getText().length()>0) conditions.setPress(press.getText());
                if(MinPublishYear.getText().length()>0) conditions.setMinPublishYear(Integer.parseInt(MinPublishYear.getText()));
                if(MaxPublishYear.getText().length()>0) conditions.setMaxPublishYear(Integer.parseInt(MaxPublishYear.getText()));
                if(author.getText().length()>0) conditions.setAuthor(author.getText());
                if(MinPrice.getText().length()>0) conditions.setMinPrice(Double.parseDouble(MinPrice.getText()));
                if(MaxPrice.getText().length()>0) conditions.setMaxPrice(Double.parseDouble(MaxPrice.getText()));
                if(sor_text.getSelectedItem()=="BookId")conditions.setSortBy(Book.SortColumn.BOOK_ID);
                if(sor_text.getSelectedItem()=="Category")conditions.setSortBy(Book.SortColumn.CATEGORY);
                if(sor_text.getSelectedItem()=="Title")conditions.setSortBy(SortColumn.TITLE);
                if(sor_text.getSelectedItem()=="Press")conditions.setSortBy(SortColumn.PRESS);
                if(sor_text.getSelectedItem()=="Publish year")conditions.setSortBy(SortColumn.PUBLISH_YEAR);
                if(sor_text.getSelectedItem()=="Author")conditions.setSortBy(SortColumn.AUTHOR);
                if(sor_text.getSelectedItem()=="Price")conditions.setSortBy(SortColumn.PRICE);
                if(sor_text.getSelectedItem()=="Stock")conditions.setSortBy(SortColumn.STOCK);
                if(sor_order_text.getSelectedItem()=="acs")conditions.setSortOrder(SortOrder.ASC);
                if(sor_order_text.getSelectedItem()=="dess")conditions.setSortOrder(SortOrder.DESC);
                ApiResult res = library.queryBook(conditions);
                String Title;
                if(res.ok) Title = "查询成功";
                else Title = "查询失败";
                JFrame resultf=new JFrame(Title);
                resultf.setSize(600,600);
                resultf.setLayout(null);
                resultf.setVisible(true);
                if(res.ok) {
                    BookQueryResults bookResults = (BookQueryResults) res.payload;
                    String[] columnNames = {"BookId", "Category", "Title", "Press", "Publish year", "Author", "Price", "Stock"};
                    String[][] data = new String[bookResults.getCount()][8];
                    for (int i = 0; i < bookResults.getCount(); i++) {
                        data[i][0] = String.valueOf(bookResults.getResults().get(i).getBookId());
                        data[i][1] = bookResults.getResults().get(i).getCategory();
                        data[i][2] = bookResults.getResults().get(i).getTitle();
                        data[i][3] = bookResults.getResults().get(i).getPress();
                        data[i][4] = String.valueOf(bookResults.getResults().get(i).getPublishYear());
                        data[i][5] = bookResults.getResults().get(i).getAuthor();
                        data[i][6] = String.valueOf(bookResults.getResults().get(i).getPrice());
                        data[i][7] = String.valueOf(bookResults.getResults().get(i).getStock());
                    }
                    JTable table = new JTable(data, columnNames);
                    JScrollPane scrollPane = new JScrollPane(table);
                    scrollPane.setBounds(0, 0, 600, 600);
                    resultf.add(scrollPane);
                } else {
                    JLabel error = new JLabel(res.message);
                    error.setBounds(0, 0, 600, 600);
                    resultf.add(error);
                }
                JButton Back=new JButton("Back");
                Back.setBounds(250,650,100,30);
                submit.add(Back);
                judge();
                showBooks();
                Back.addActionListener(e13 -> submit.setVisible(false));
            });
            queryf.setSize(300,500);
            queryf.setLayout(null);
            queryf.setVisible(true);
        });
        query.setVisible(false);
        query.setBounds(600,575,150,50);
        f.add(query);
    }

    public void borrowBook() {
        borrow=new JButton("借书");
        borrow.addActionListener(e -> {
            JFrame borf=new JFrame("借书");
//                    设置label
            JLabel bid = new JLabel("bookID:");
            JLabel cid = new JLabel("cardID:");
//                    设置大小
            bid.setBounds(10,50,90,30);
            cid.setBounds(10,85,90,30);
//                    加进frame
            borf.add(bid);
            borf.add(cid);
//                    文本框
            JTextField bookID=new JTextField();
            JTextField cardID=new JTextField();
            bookID.setBounds(100,50,150,30);
            cardID.setBounds(100,85,150,30);
            borf.add(bookID);
            borf.add(cardID);
//                    提交
            JButton submit = new JButton("提交");
            submit.setBounds(50,295,150,30);
            borf.add(submit);
            submit.addActionListener(e12 -> {
                borf.setVisible(false);
                Borrow nb = new Borrow(
                        Integer.parseInt(bookID.getText()),
                        Integer.parseInt(cardID.getText())
                );
                nb.setBorrowTime(getTime());
                nb.resetReturnTime();
                ApiResult res = library.borrowBook(nb);
                String res_title;
                if(res.ok) {
                    res_title = "Success borrow";
                    System.out.print("Success to borrow book\n");
                }
                else {
                    res_title = "Failed to borrow";
                    System.out.print("Failed to borrow book\n");
                }
                JFrame res_submit = new JFrame(res_title);
                res_submit.setSize(350,300);
                res_submit.setLayout(null);
                res_submit.setVisible(true);
                JLabel res_label=new JLabel();
                res_label.setText(res_title);
                res_label.setFont(new Font(res_label.getFont().getName(),res_label.getFont().getStyle(),15));
                res_label.setBounds(0,100,300,30);
                res_label.setHorizontalAlignment(JLabel.CENTER);
                res_submit.add(res_label);
                JButton Back=new JButton("Back");
                Back.setBounds(100,200,100,30);
                res_submit.add(Back);
                judge();
                showBorrows();
                Back.addActionListener(e1 -> res_submit.setVisible(false));
            });
            borf.setSize(300,500);
            borf.setLayout(null);
            borf.setVisible(true);
        });
        borrow.setVisible(false);
        borrow.setBounds(200,500,150,50);
        f.add(borrow);
    }

    public void returnBook() {
        return_book=new JButton("还书");
        return_book.addActionListener(e -> {
            JFrame retf=new JFrame("还书");
//                    设置label
            JLabel bid = new JLabel("bookID:");
            JLabel cid = new JLabel("cardID:");
            JLabel bor = new JLabel("Borrow Time:");
//                    设置大小
            bid.setBounds(10,50,90,30);
            cid.setBounds(10,85,90,30);
            bor.setBounds(10,120,90,30);
//                    加进frame
            retf.add(bid);
            retf.add(cid);
            retf.add(bor);
//                    文本框
            JTextField bookID=new JTextField();
            JTextField cardID=new JTextField();
            JTextField borrowTime=new JTextField();
            bookID.setBounds(100,50,150,30);
            cardID.setBounds(100,85,150,30);
            borrowTime.setBounds(100,120,150,30);
            retf.add(bookID);
            retf.add(cardID);
            retf.add(borrowTime);
//                    提交
            JButton submit = new JButton("提交");
            submit.setBounds(50,295,150,30);
            retf.add(submit);
            submit.addActionListener(e12 -> {
                retf.setVisible(false);
                Borrow nb = new Borrow(
                        Integer.parseInt(bookID.getText()),
                        Integer.parseInt(cardID.getText())
                );
                nb.setBorrowTime(Long.parseLong(borrowTime.getText()));
                nb.setReturnTime(getTime());
                ApiResult res = library.returnBook(nb);
                String res_title;
                if(res.ok) {
                    res_title = "Success return";
                    System.out.print("Success to return book\n");
                }
                else {
                    res_title = "Failed to return";
                    System.out.print("Failed to return book\n");
                }
                JFrame res_submit = new JFrame(res_title);
                res_submit.setSize(350,300);
                res_submit.setLayout(null);
                res_submit.setVisible(true);
                JLabel res_label=new JLabel();
                res_label.setText(res_title);
                res_label.setFont(new Font(res_label.getFont().getName(),res_label.getFont().getStyle(),15));
                res_label.setBounds(0,100,300,30);
                res_label.setHorizontalAlignment(JLabel.CENTER);
                res_submit.add(res_label);
                JButton Back=new JButton("Back");
                Back.setBounds(100,200,100,30);
                res_submit.add(Back);
                judge();
                showBorrows();
                Back.addActionListener(e1 -> res_submit.setVisible(false));
            });
            retf.setSize(300,500);
            retf.setLayout(null);
            retf.setVisible(true);
        });
        return_book.setVisible(false);
        return_book.setBounds(400,500,150,50);
        f.add(return_book);
    }

    public void showBorrowHistory() {
        queryBorrow=new JButton("查询借书历史");
        queryBorrow.addActionListener(e -> {
            JFrame queryf=new JFrame("查询借书历史");
//                    设置label
            JLabel cid = new JLabel("cardID:");
//                    设置大小
            cid.setBounds(10,15,90,30);
//                    加进frame
            queryf.add(cid);
//                    文本框
            JTextField cardID=new JTextField();
            cardID.setBounds(100,15,150,30);
            queryf.add(cardID);
//                    提交
            JButton submit = new JButton("提交");
            submit.setBounds(50,365,150,30);
            queryf.add(submit);
            submit.addActionListener(e12 -> {
                queryf.setVisible(false);
                ApiResult res = library.showBorrowHistory(Integer.parseInt(cardID.getText()));
                String Title;
                if(res.ok) Title = "查询成功";
                else Title = "查询失败";
                JFrame resultf=new JFrame(Title);
                resultf.setSize(1000,600);
                resultf.setLayout(null);
                resultf.setVisible(true);
                if(res.ok) {
                    BorrowHistories borrowhistories = (BorrowHistories) res.payload;
                    String[] columnNames = {"cardId", "bookId", "Category", "Title", "Press", "Publish year", "Author", "Price", "borrowTime", "returnTime"};
                    String[][] data = new String[borrowhistories.getCount()][10];
                    for (int i = 0; i < borrowhistories.getCount(); i++) {
                        data[i][0] = String.valueOf(borrowhistories.getItems().get(i).getCardId());
                        data[i][1] = String.valueOf(borrowhistories.getItems().get(i).getBookId());
                        data[i][2] = borrowhistories.getItems().get(i).getCategory();
                        data[i][3] = borrowhistories.getItems().get(i).getTitle();
                        data[i][4] = borrowhistories.getItems().get(i).getPress();
                        data[i][5] = String.valueOf(borrowhistories.getItems().get(i).getPublishYear());
                        data[i][6] = borrowhistories.getItems().get(i).getAuthor();
                        data[i][7] = String.valueOf(borrowhistories.getItems().get(i).getPrice());
                        data[i][8] = String.valueOf(borrowhistories.getItems().get(i).getBorrowTime());
                        data[i][9] = String.valueOf(borrowhistories.getItems().get(i).getReturnTime());
                    }
                    JTable table = new JTable(data, columnNames);
                    JScrollPane scrollPane = new JScrollPane(table);
                    scrollPane.setBounds(0, 0, 1000, 600);
                    resultf.add(scrollPane);
                } else {
                    JLabel error = new JLabel(res.message);
                    error.setBounds(0, 0, 1000, 600);
                    resultf.add(error);
                }
                JButton Back=new JButton("Back");
                Back.setBounds(250,650,100,30);
                submit.add(Back);
                judge();
                showBorrows();
                Back.addActionListener(e13 -> submit.setVisible(false));
            });
            queryf.setSize(300,500);
            queryf.setLayout(null);
            queryf.setVisible(true);
        });
        queryBorrow.setVisible(false);
        queryBorrow.setBounds(600,500,150,50);
        f.add(queryBorrow);
    }

    public void registerCard() {
        register=new JButton("注册借书卡");
        register.addActionListener(e -> {
            JFrame regf=new JFrame("注册借书卡");
//                    设置label
            JLabel name = new JLabel("name:");
            JLabel depart = new JLabel("department:");
            JLabel type = new JLabel("type:");
//                    设置大小
            name.setBounds(10,50,90,30);
            depart.setBounds(10,85,90,30);
            type.setBounds(10,120,90,30);
//                    加进frame
            regf.add(name);
            regf.add(depart);
            regf.add(type);
//                    文本框
            JTextField name_text=new JTextField();
            JTextField depart_text=new JTextField();
            JComboBox<String> type_select= new JComboBox<>();
            type_select.addItem("Teacher");
            type_select.addItem("Student");
            name_text.setBounds(100,50,150,30);
            depart_text.setBounds(100,85,150,30);
            type_select.setBounds(100,120,150,30);
            regf.add(name_text);
            regf.add(depart_text);
            regf.add(type_select);
//                    提交
            JButton submit = new JButton("提交");
            submit.setBounds(50,295,150,30);
            regf.add(submit);
            submit.addActionListener(e12 -> {
                regf.setVisible(false);
                Card nc = null;
                if(type_select.getSelectedItem() == "Teacher") {
                    nc = new Card(
                            0,
                            name_text.getText(),
                            depart_text.getText(),
                            Card.CardType.Teacher
                    );
                } else if(type_select.getSelectedItem() == "Student") {
                    nc = new Card(
                            0,
                            name_text.getText(),
                            depart_text.getText(),
                            Card.CardType.Student
                    );
                }
                ApiResult res = library.registerCard(nc);
                String res_title;
                if(res.ok) {
                    res_title = "Success register";
                    System.out.print("Success to register\n");
                }
                else {
                    res_title = "Failed to register";
                    System.out.print("Failed to register\n");
                }
                JFrame res_submit = new JFrame(res_title);
                res_submit.setSize(350,300);
                res_submit.setLayout(null);
                res_submit.setVisible(true);
                JLabel res_label=new JLabel();
                res_label.setText(res_title);
                res_label.setFont(new Font(res_label.getFont().getName(),res_label.getFont().getStyle(),15));
                res_label.setBounds(0,100,300,30);
                res_label.setHorizontalAlignment(JLabel.CENTER);
                res_submit.add(res_label);
                JButton Back=new JButton("Back");
                Back.setBounds(100,200,100,30);
                res_submit.add(Back);
                judge();
                showCards();
                Back.addActionListener(e1 -> res_submit.setVisible(false));
            });
            regf.setSize(300,500);
            regf.setLayout(null);
            regf.setVisible(true);
        });
        register.setVisible(false);
        register.setBounds(200,500,150,50);
        f.add(register);
    }

    public void removeCard() {
        remove=new JButton("注销借书卡");
        remove.addActionListener(e -> {
            JFrame rmvf=new JFrame("注销借书卡");
//                    设置label
            JLabel ID = new JLabel("cardID:");
//                    设置大小
            ID.setBounds(10,50,90,30);
//                    加进frame
            rmvf.add(ID);
//                    文本框
            JTextField cardID=new JTextField();
            cardID.setBounds(100,50,150,30);
            rmvf.add(cardID);
//                    提交
            JButton submit = new JButton("提交");
            submit.setBounds(50,295,150,30);
            rmvf.add(submit);
            submit.addActionListener(e12 -> {
                rmvf.setVisible(false);
                ApiResult res = library.removeCard(Integer.parseInt(cardID.getText()));
                String res_title;
                if (res.ok) {
                    res_title = "Success remove card";
                    System.out.print("Success to remove card\n");
                } else {
                    res_title = "Failed to remove card";
                    System.out.print("Failed to remove card\n");
                }
                JFrame res_submit = new JFrame(res_title);
                res_submit.setSize(350, 300);
                res_submit.setLayout(null);
                res_submit.setVisible(true);
                JLabel res_label = new JLabel();
                res_label.setText(res_title);
                res_label.setFont(new Font(res_label.getFont().getName(), res_label.getFont().getStyle(), 15));
                res_label.setBounds(0, 100, 300, 30);
                res_label.setHorizontalAlignment(JLabel.CENTER);
                res_submit.add(res_label);
                JButton Back = new JButton("Back");
                Back.setBounds(100, 200, 100, 30);
                res_submit.add(Back);
                judge();
                showCards();
                Back.addActionListener(e1 -> res_submit.setVisible(false));
            });
            rmvf.setSize(300,500);
            rmvf.setLayout(null);
            rmvf.setVisible(true);
        });
        remove.setVisible(false);
        remove.setBounds(400,500,150,50);
        f.add(remove);
    }

    public void showCards() {
        ApiResult res = library.showCards();
        if(res.ok) {
            CardList resCardList = (CardList) res.payload;
            String[] columnNames = {"cardId", "Name", "Department", "Type"};
            String[][] data = new String[resCardList.getCount()][4];
            for (int i = 0; i < resCardList.getCount(); i++) {
                data[i][0] = String.valueOf(resCardList.getCards().get(i).getCardId());
                data[i][1] = resCardList.getCards().get(i).getName();
                data[i][2] = resCardList.getCards().get(i).getDepartment();
                if(resCardList.getCards().get(i).getType() == Card.CardType.Student) data[i][3] = "Student";
                else if(resCardList.getCards().get(i).getType() == Card.CardType.Teacher) data[i][3] = "Teacher";
            }
            JTable table = new JTable(data, columnNames);
            scrollPane_card = new JScrollPane(table);
            scrollPane_card.setBounds(200, 100, 750, 350);
            f.add(scrollPane_card);
            scrollPane_card.setVisible(true);
        } else {
            JLabel error = new JLabel(res.message);
            error.setBounds(0, 0, 1000, 600);
            f.add(error);
        }
    }

    public void showBooks() {
        ApiResult res = library.showBooks();
        if(res.ok) {
            BookQueryResults bookList = (BookQueryResults) res.payload;
            String[] columnNames = {"BookId", "Category", "Title", "Press",
                    "Publish year", "Author", "Price", "Stock"};
            String[][] data = new String[bookList.getCount()][8];
            for (int i = 0; i < bookList.getCount(); i++) {
                data[i][0] = String.valueOf(bookList.getResults().get(i).getBookId());
                data[i][1] = bookList.getResults().get(i).getCategory();
                data[i][2] = bookList.getResults().get(i).getTitle();
                data[i][3] = bookList.getResults().get(i).getPress();
                data[i][4] = String.valueOf(bookList.getResults().get(i).getPublishYear());
                data[i][5] = bookList.getResults().get(i).getAuthor();
                data[i][6] = String.valueOf(bookList.getResults().get(i).getPrice());
                data[i][7] = String.valueOf(bookList.getResults().get(i).getStock());
            }
            JTable table = new JTable(data, columnNames);
            scrollPane_book = new JScrollPane(table);
            scrollPane_book.setBounds(200, 100, 750, 350);
            f.add(scrollPane_book);
            scrollPane_book.setVisible(true);
        } else {
            JLabel error = new JLabel(res.message);
            error.setBounds(0, 0, 1000, 600);
            f.add(error);
        }
    }

    public void showBorrows() {
        ApiResult res = library.showBorrows();
        if(res.ok) {
            BorrowHistories borrowList = (BorrowHistories) res.payload;
            String[] columnNames = {"cardId", "BookId", "Category", "Title", "Press",
                    "Publish year", "Author", "Price", "borrowTime", "returnTime"};
            String[][] data = new String[borrowList.getCount()][10];
            for (int i = 0; i < borrowList.getCount(); i++) {
                data[i][0] = String.valueOf(borrowList.getItems().get(i).getCardId());
                data[i][1] = String.valueOf(borrowList.getItems().get(i).getBookId());
                data[i][2] = borrowList.getItems().get(i).getCategory();
                data[i][3] = borrowList.getItems().get(i).getTitle();
                data[i][4] = borrowList.getItems().get(i).getPress();
                data[i][5] = String.valueOf(borrowList.getItems().get(i).getPublishYear());
                data[i][6] = borrowList.getItems().get(i).getAuthor();
                data[i][7] = String.valueOf(borrowList.getItems().get(i).getPrice());
                data[i][8] = String.valueOf(borrowList.getItems().get(i).getBorrowTime());
                data[i][9] = String.valueOf(borrowList.getItems().get(i).getReturnTime());
            }
            JTable table = new JTable(data, columnNames);
            scrollPane_borrow = new JScrollPane(table);
            scrollPane_borrow.setBounds(200, 100, 750, 350);
            f.add(scrollPane_borrow);
            scrollPane_borrow.setVisible(true);
        } else {
            JLabel error = new JLabel(res.message);
            error.setBounds(0, 0, 1000, 600);
            f.add(error);
        }
    }
    public void sidebar() {
        JLabel title = new JLabel("图书管理系统");
        title.setFont(new Font(title.getFont().getName(), title.getFont().getStyle(), 35));
        title.setBounds(30,10,300,100);
        f.add(title);

        if(scrollPane_book != null)scrollPane_book.setVisible(false);
        if(scrollPane_borrow != null)scrollPane_borrow.setVisible(false);
        if(scrollPane_card != null)scrollPane_card.setVisible(false);

        JButton bookManage = new JButton("图书管理");
        bookManage.setBounds(10,200,150,50);
        bookManage.setFocusPainted(false);
        bookManage.setBorderPainted(false);
        f.add(bookManage);
        bookManage.addActionListener(e -> {
            add.setVisible(true);
            incBook.setVisible(true);
            adds.setVisible(true);
            removeBook.setVisible(true);
            modify.setVisible(true);
            query.setVisible(true);
            borrow.setVisible(false);
            return_book.setVisible(false);
            queryBorrow.setVisible(false);
            register.setVisible(false);
            remove.setVisible(false);
            if(scrollPane_borrow != null) scrollPane_borrow.setVisible(false);
            if(scrollPane_card != null) scrollPane_card.setVisible(false);
            if(scrollPane_book != null) scrollPane_book.setVisible(false);
            scrollPane_book = null;
            scrollPane_borrow = null;
            scrollPane_card = null;
            showBooks();
        });

        JButton cardManage = new JButton("借书证管理");
        cardManage.setBounds(10,300,150,50);
        cardManage.setFocusPainted(false);
        cardManage.setBorderPainted(false);
        f.add(cardManage);
        cardManage.addActionListener(e -> {
            add.setVisible(false);
            incBook.setVisible(false);
            adds.setVisible(false);
            removeBook.setVisible(false);
            modify.setVisible(false);
            query.setVisible(false);
            borrow.setVisible(false);
            return_book.setVisible(false);
            queryBorrow.setVisible(false);
            register.setVisible(true);
            remove.setVisible(true);
            if(scrollPane_borrow != null) scrollPane_borrow.setVisible(false);
            if(scrollPane_card != null) scrollPane_card.setVisible(false);
            if(scrollPane_book != null) scrollPane_book.setVisible(false);
            scrollPane_book = null;
            scrollPane_borrow = null;
            scrollPane_card = null;
            showCards();
        });

        JButton bookOperation = new JButton("借书还书");
        bookOperation.setBounds(10,400,150,50);
        bookOperation.setFocusPainted(false);
        bookOperation.setBorderPainted(false);
        f.add(bookOperation);
        bookOperation.addActionListener(e -> {
            add.setVisible(false);
            incBook.setVisible(false);
            adds.setVisible(false);
            removeBook.setVisible(false);
            modify.setVisible(false);
            query.setVisible(false);
            borrow.setVisible(true);
            return_book.setVisible(true);
            queryBorrow.setVisible(true);
            register.setVisible(false);
            remove.setVisible(false);
            if(scrollPane_borrow != null) scrollPane_borrow.setVisible(false);
            if(scrollPane_card != null) scrollPane_card.setVisible(false);
            if(scrollPane_book != null) scrollPane_book.setVisible(false);
            scrollPane_book = null;
            scrollPane_borrow = null;
            scrollPane_card = null;
            showBorrows();
        });
    }

    Main() {
        try {
            // parse connection config from "resources/application.yaml"
            ConnectConfig conf = new ConnectConfig();
            log.info("Success to parse connect config. " + conf);
            // connect to database
            connector = new DatabaseConnector(conf);
            library = new LibraryManagementSystemImpl(connector);
            boolean connStatus = connector.connect();
            if (!connStatus) {
                log.severe("Failed to connect database.");
                System.exit(1);
            }

            /* Create JFrame*/
//            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            reset();
            storeBook();
            incBookStock();
            storeBookList();
            removeBook();
            modifyBook();
            queryBook();
            borrowBook();
            returnBook();
            showBorrowHistory();
            registerCard();
            removeCard();
            showCards();
            sidebar();

            f.setSize(1000,700);
            f.setLayout(null);
            f.setVisible(true);

            // release database connection handler
            f.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    if (connector.release()) {
                        log.info("Success to release connection.");
                    } else {
                        log.warning("Failed to release connection.");
                    }
                    System.exit(0);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        new Main();
    }

}