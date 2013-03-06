package Game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import Game.Shape.Tetrominoes;


public class Board extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;
	final int BoardWidth = 10;
    final int BoardHeight = 15;

    Timer timer;
    boolean isFallingFinished = false;
    boolean isStarted = false;
    int numLinesRemoved = 0;
    int curX = 0;
    int curY = 0;
    JLabel statusbar;
    Shape curPiece;
    Tetrominoes[] board;



    public Board(Tetris parent) {

       setFocusable(true);
       curPiece = new Shape();
       timer = new Timer(400, this);
       timer.start(); 

       statusbar =  parent.getStatusBar();
       board = new Tetrominoes[BoardWidth * BoardHeight];
       addKeyListener(new TAdapter());
       clearBoard();  
    }

    public void actionPerformed(ActionEvent e) {
        if (isFallingFinished) {
            isFallingFinished = false;
            newPiece();
        } else {
            oneLineDown();
        }
    }


    int squareWidth() { return (int) getSize().getWidth() / BoardWidth; }
    int squareHeight() { return (int) getSize().getHeight() / BoardHeight; }
    Tetrominoes shapeAt(int x, int y) { return board[(y * BoardWidth) + x]; }


    public void start()
    {
        isStarted = true;
        isFallingFinished = false;
        numLinesRemoved = 0;
        clearBoard();

        newPiece();
        timer.start();
    }

    public void paint(Graphics g)
    { 
        super.paint(g);

        Dimension size = getSize();
        int boardTop = (int) size.getHeight() - BoardHeight * squareHeight();


        for (int i = 0; i < BoardHeight; ++i) {
            for (int j = 0; j < BoardWidth; ++j) {
                Tetrominoes shape = shapeAt(j, BoardHeight - i - 1);
                if (shape != Tetrominoes.NoShape)
                    drawSquare(g, 0 + j * squareWidth(),
                               boardTop + i * squareHeight(), shape);
            }
        }

        if (curPiece.getShape() != Tetrominoes.NoShape) {
            for (int i = 0; i < 4; ++i) {
                int x = curX + curPiece.x(i);
                int y = curY - curPiece.y(i);
                drawSquare(g, 0 + x * squareWidth(),
                           boardTop + (BoardHeight - y - 1) * squareHeight(),
                           curPiece.getShape());
            }
        }
    }

    private void dropDown()
    {
        int newY = curY;
        while (newY > 0) {
            if (!tryMove(curPiece, curX, newY - 1))
                break;
            --newY;
        }
        pieceDropped();
    }

    private void oneLineDown()
    {
        if (!tryMove(curPiece, curX, curY - 1))
            pieceDropped();
    }


    private void clearBoard()
    {
        for (int i = 0; i < BoardHeight * BoardWidth; ++i)
            board[i] = Tetrominoes.NoShape;
    }

    private void pieceDropped()
    {
        for (int i = 0; i < 4; ++i) {
            int x = curX + curPiece.x(i);
            int y = curY - curPiece.y(i);
            board[(y * BoardWidth) + x] = curPiece.getShape();
        }

        removeFullLines();

        if (!isFallingFinished)
            newPiece();
    }

    private void newPiece()
    {
        curPiece.setRandomShape();
        curX = BoardWidth / 2 + 1;
        curY = BoardHeight - 1 + curPiece.minY();

        if (!tryMove(curPiece, curX, curY)) {
            curPiece.setShape(Tetrominoes.NoShape);
            timer.stop();
            isStarted = false;
            statusbar.setText("Game over! Lines: " + String.valueOf(numLinesRemoved));
        }
    }

    private boolean tryMove(Shape newPiece, int newX, int newY)
    {
        for (int i = 0; i < 4; ++i) {
            int x = newX + newPiece.x(i);
            int y = newY - newPiece.y(i);
            if (x < 0 || x >= BoardWidth || y < 0 || y >= BoardHeight)
                return false;
            if (shapeAt(x, y) != Tetrominoes.NoShape)
                return false;
        }

        curPiece = newPiece;
        curX = newX;
        curY = newY;
        repaint();
        return true;
    }

    private void removeFullLines()
    {
        int numFullLines = 0;

        for (int i = BoardHeight - 1; i >= 0; --i) {
            boolean isFull = true;

            for (int j = 0; j < BoardWidth; ++j) {
                if (shapeAt(j, i) == Tetrominoes.NoShape) {
                	isFull = false;
                    break;
                }
            }

            if (isFull) {
                ++numFullLines;
                for (int k = i; k < BoardHeight - 1; ++k) {
                    for (int j = 0; j < BoardWidth; ++j)
                         board[(k * BoardWidth) + j] = shapeAt(j, k + 1);
                }
            }
        }

        if (numFullLines > 0) {
            numLinesRemoved += numFullLines;
            statusbar.setText(" Lines: " + String.valueOf(numLinesRemoved));
            isFallingFinished = true;
            curPiece.setShape(Tetrominoes.NoShape);
            repaint();
        }
     }

    private void drawSquare(Graphics g, int x, int y, Tetrominoes shape)
    {
        Color colors[] = { 
        	new Color(0, 0, 0), // NoShape      
            new Color(60, 188, 252), //ZShape
            new Color(0, 144, 56), //SShape
            new Color(216, 40, 0), //LineShape
            new Color(136, 112, 0), //TShape
            new Color(32, 56, 236), //SquareShape
            new Color(244, 120, 252), //LShape
            new Color(252, 152, 56) //MirroredLShape
            
        	/*new Color(0, 0, 0), // NoShape      
        	new Color(204, 102, 102), //ZShape
            new Color(102, 204, 102), //SShape
            new Color(102, 102, 204), //LineShape
            new Color(204, 204, 102), //TShape
            new Color(204, 102, 204), //SquareShape
            new Color(102, 204, 204), //LShape
            new Color(218, 170, 0) //MirroredLShape*/
        };


        Color color = colors[shape.ordinal()];

        g.setColor(color);
        g.fillRect(x + 1, y + 1, squareWidth() - 2, squareHeight() - 2);

//        g.setColor(color.brighter());
//        g.drawLine(x, y + squareHeight() - 1, x, y);
//        g.drawLine(x, y, x + squareWidth() - 1, y);
//
//        g.setColor(color.darker());
//        g.drawLine(x + 1, y + squareHeight() - 1,
//                         x + squareWidth() - 1, y + squareHeight() - 1);
//        g.drawLine(x + squareWidth() - 1, y + squareHeight() - 1,
//                         x + squareWidth() - 1, y + 1);

    }

    class TAdapter extends KeyAdapter {
         public void keyPressed(KeyEvent e) {

             if (!isStarted || curPiece.getShape() == Tetrominoes.NoShape) {  
                 return;
             }

             int keycode = e.getKeyCode();

             switch (keycode) {
             	case KeyEvent.VK_LEFT:
             		tryMove(curPiece, curX - 1, curY);
             		break;
             	case KeyEvent.VK_RIGHT:
             		tryMove(curPiece, curX + 1, curY);
             		break;
             	case KeyEvent.VK_DOWN:
             		oneLineDown();
             		break;
             	case KeyEvent.VK_UP:
             		dropDown();
             		break;
             	case KeyEvent.VK_SPACE:
             		tryMove(curPiece.rotateRight(), curX, curY);
             		break;
             }

         }
     }
}