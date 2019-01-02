package com.compiler;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Stack;

/**
 * 词法分析器
 */
public class LexAnalyse {

	public ArrayList<Word> wordList = new ArrayList<Word>();// 单词表
	public ArrayList<Error> errorList = new ArrayList<Error>();// 错误信息列表
	public static ArrayList<String> intlist = new ArrayList<String>();// int集合
	public static ArrayList<String> charlist = new ArrayList<String>();// char集合
	public static ArrayList<Typeword> typelist = new ArrayList<Typeword>();// char集合
	public int wordCount = 0;// 统计单词个数
	public int errorCount = 0;// 统计错误个数
	public boolean noteFlag = false;// 多行注释标志
	public boolean lexErrorFlag = false;// 词法分析出错标志
	public Stack<String>  typestack=new Stack<String>();//类型栈
	public LexAnalyse() {
	}

	public LexAnalyse(String str) {     //对Str字符串进行词法分析
		lexAnalyse(str);
	}
	/**
	 * 数字字符判断
	 */
	private static boolean isDigit(char ch) {
		boolean flag = false;     //先设判断标志为false
		if ('0' <= ch && ch <= '9')
			flag = true;
		return flag;
	}

	/**
	 * 判断单词是否为int常量
	 */
	private static boolean isInteger(String word) {   //传入数字123(1个word)
		int i;
		boolean flag = false;
		for (i = 0; i < word.length(); i++) {    //对word每一个位进行判断
			if (Character.isDigit(word.charAt(i))) {     //判断第i位的字符是否为数字，是数字继续判断下一位
                continue;                               // ，不是的话就跳出循环，判断标志仍为false
			} else {
				break;
			}
		}
		if (i == word.length()) {        //i=0的情况，为int型常量
			flag = true;
		}
		return flag;
	}

	/**
	 * 判断单词是否为char常量
	 */       //看不懂
//	private static boolean isChar(String word) { //入参为一个单词
//		boolean flag = false;
//		int i = 0;
//		char temp = word.charAt(i);    //第i位字符
//		if (temp == '\'') {         //
//			for (i = 1; i < word.length(); i++) {
//				temp = word.charAt(i);
//				if (0 <= temp && temp <= 255)
//					continue;
//				else
//					break;
//			}
//			if (i + 1 == word.length() && word.charAt(i) == '\'')
//				flag = true;
//		} else
//			return flag;
//
//		return flag;
//	}

	/**
	 * 判断字符是否为字母
	 */
	private static boolean isLetter(char ch) {
		boolean flag = false;
		if (('a' <= ch && ch <= 'z') || ('A' <= ch && ch <= 'Z'))
			flag = true;
		return flag;
	}

	/**
	 * 判断单词是否为合法标识符
	 */      //有点问题
	private static boolean isID(String word) {
		boolean flag = false;
		int i = 0;
		if (Word.isKey(word))     //判断单词是否是关键字
			return flag;
		char temp = word.charAt(i);     //取第i位字符
		if (isLetter(temp) || temp == '_') {        //判断该字符是不是字母或者？
			for (i = 1; i < word.length(); i++) {     //对从第一位开始的每一位字符判断是否是字符数字还是？
				temp = word.charAt(i);
				if (isLetter(temp) || temp == '_' || isDigit(temp))
					continue;

				else
					break;
			}
			if (i >= word.length())    //如果这个单词只有一位的话肯定是合法标识符
				flag = true;
		} else
			return flag;

		return flag;
	}

	/**
	 * 判断词法分析是否通过
	 */
	public boolean isFail() {
		return lexErrorFlag;      //初始是false

	}

	public void analyse(String str, int line) {     //传入第一行 str{ int sum=123 } line=1
		int beginIndex;
		int endIndex;
		int index = 0;     //相当于单个字符的序号，从第0个单词开始  int
		int length = str.length();  //length=9
		Word word = null;
		Typeword typeword=null;
		Error error;
		// boolean flag=false;
		char temp;
		while (index < length) {        //0<9
			temp = str.charAt(index);    //temp=i
			if (!noteFlag) {     //true
				if (isLetter(temp) || temp == '_') {// 判断temp=i是字母还是_
					beginIndex = index;       //beginIndex=0,从第0个单词开始
					index++;        //index=1
					while ((index < length)                         //  1<9 判断单词是否为操作符、界符还是特殊字符
							&& (!Word.isBoundarySign(str.substring(index,  //判断index=1处（n）是不是界符  (t)   (' ')
									index + 1)))
							&& (!Word.isOperator(str
									.substring(index, index + 1)))
							&& (str.charAt(index) != ' ')
							&& (str.charAt(index) != '\t')
							&& (str.charAt(index) != '\r')
							&& (str.charAt(index) != '\n')) {
						index++; //index=2   index=3
						// temp=str.charAt(index);
					}
					endIndex = index;  //endIndex=3
					word = new Word();
					wordCount++;           //wordCount=1
					word.id = wordCount;    //word.id=1
					word.line = line;       //word.line=1
					word.value = str.substring(beginIndex, endIndex);  //word.value=int
					if (Word.isKey(word.value)) {          //判断int是否为关键字
						word.type = Word.KEY;                //word.type=word.KEY
						if(word.value.equals("int")||word.value.equals("char")){
							typestack.push(word.value);       //typestack中入栈int
						}
					} else if (isID(word.value)) {           //判断main是否为标识符
						word.type = Word.IDENTIFIER;
						if(typestack.size()>0){             //int sum 此时typestack.size=1
							 word.attribute=typestack.lastElement();   //word.attribute=int
							 if(word.attribute.equals("int")||word.attribute.equals("char")){
								 typeword=new Typeword();
								 typeword.value=word.value;     //sum为typeword
								 typeword.type=word.attribute;
								 typelist.add(typeword);
							 }	 
						}
					} else {              //
						word.type = Word.UNIDEF;
						word.flag = false;
						errorCount++;
						error = new Error(errorCount, "非法标识符", word.line, word);
						errorList.add(error);
						lexErrorFlag = true;
					}
					index--;

				} else if (isDigit(temp)) {// 判断是不是int常数

					beginIndex = index;
					index++;
					// temp=str.charAt(index);
					while ((index < length)
							&& (!Word.isBoundarySign(str.substring(index,
									index + 1)))
							&& (!Word.isOperator(str
									.substring(index, index + 1)))
							&& (str.charAt(index) != ' ')
							&& (str.charAt(index) != '\t')
							&& (str.charAt(index) != '\r')
							&& (str.charAt(index) != '\n')) {
						index++;
						// temp=str.charAt(index);
					}
					endIndex = index;
					word = new Word();
					wordCount++;      //
					word.id = wordCount;
					word.line = line;
					word.value = str.substring(beginIndex, endIndex);
					if (isInteger(word.value)) {
						word.type = Word.INT_CONST;
					} else {
						word.type = Word.UNIDEF;
						word.flag = false;
						errorCount++;
						error = new Error(errorCount, "非法标识符", word.line, word);
						errorList.add(error);
						lexErrorFlag = true;
					}
					index--;
				} else if (str.charAt(index)=='\'') {// 字符常量   字符常量是哪些
					// flag=true;
					beginIndex = index;
					index++;
					temp = str.charAt(index);
					if(index < length && isLetter(temp)) {
						index++;
						if (index < length&&str.charAt(index)=='\''){
							word = new Word();
							wordCount++;
							word.id = wordCount;
							word.line = line;
							word.value = str.substring(beginIndex, index+1);
							word.type = Word.CHAR_CONST;
						}
						else {
							endIndex = index;
							word = new Word();
							wordCount++;
							word.id = wordCount;
							word.line = line;
							word.value = str.substring(beginIndex, index+1);
							word.type = Word.UNIDEF;
							word.flag = false;
							errorCount++;
							error = new Error(errorCount, "非法标识符", word.line, word);
							errorList.add(error);
							lexErrorFlag = true;
						}
						// temp=str.charAt(index);
					}
					else {
						word = new Word();
						wordCount++;
						word.id = wordCount;
						word.line = line;
						word.value = str.substring(beginIndex, index+1);
						word.type = Word.UNIDEF;
						word.flag = false;
						errorCount++;
						error = new Error(errorCount, "非法标识符", word.line, word);
						errorList.add(error);
						lexErrorFlag = true;
					}
					
				} 
				else if (str.charAt(index)=='"') {// scanf格式判断
					// flag=true;
					beginIndex = index;
					index++;
					//int fast=index;
					temp = str.charAt(index);
					while (index < length) {
						if(str.charAt(index)=='%'){
							index++;
							if(index<length&&str.charAt(index)=='d'|str.charAt(index)=='c'){
								index++;
								if(str.charAt(index)=='"'){
									endIndex = index;
									word = new Word();
									wordCount++;
									word.id = wordCount;
									word.line = line;
									word.value = str.substring(beginIndex, index+1);
									word.type = Word.KEY;
									break;
								}
							}
							else{
								word = new Word();
								wordCount++;
								word.id = wordCount;
								word.line = line;
								word.value = str.substring(beginIndex, index);
								word.type = Word.UNIDEF;
								word.flag = false;
								errorCount++;
								error = new Error(errorCount, "非法标识符", word.line, word);
								errorList.add(error);
								lexErrorFlag = true;
								break;
							}
						}
						else{
							word = new Word();
							wordCount++;
							word.id = wordCount;
							word.line = line;
							word.value = str.substring(beginIndex, index);
							word.type = Word.UNIDEF;
							word.flag = false;
							errorCount++;
							error = new Error(errorCount, "非法标识符", word.line, word);
							errorList.add(error);
							lexErrorFlag = true;
							break;
						}
						// temp=str.charAt(index);
					}
				} 
				else if (temp == '=') {
					beginIndex = index;
					index++;
					if (index < length && str.charAt(index) == '=') {
						endIndex = index + 1;
						word = new Word();
						wordCount++;
						word.id = wordCount;
						word.line = line;
						word.value = str.substring(beginIndex, endIndex);
						word.type = Word.OPERATOR;
					} else {
						// endIndex=index;
						word = new Word();
						wordCount++;
						word.id = wordCount;
						word.line = line;
						word.value = str.substring(index - 1, index);
						word.type = Word.OPERATOR;
						index--;
					}
				} else if (temp == '!') {
					beginIndex = index;
					index++;
					if (index < length && str.charAt(index) == '=') {
						endIndex = index + 1;
						word = new Word();
						wordCount++;
						word.id = wordCount;
						word.line = line;
						word.value = str.substring(beginIndex, endIndex);
						word.type = Word.OPERATOR;
						index++;
					} else {
						// endIndex=index;
						word = new Word();
						wordCount++;
						word.id = wordCount;
						word.line = line;
						word.value = str.substring(index - 1, index);
						word.type = Word.OPERATOR;
						index--;
					}
				} else if (temp == '&') {
					beginIndex = index;
					index++;
					if (index < length && str.charAt(index) == '&') {
						endIndex = index + 1;
						word = new Word();
						wordCount++;
						word.id = wordCount;
						word.line = line;
						word.value = str.substring(beginIndex, endIndex);
						word.type = Word.OPERATOR;
					} else {
						// endIndex=index;
						word = new Word();
						wordCount++;
						word.id = wordCount;
						word.line = line;
						word.value = str.substring(index - 1, index);
						word.type = Word.OPERATOR;
						index--;
					}
				} else if (temp == '|') {
					beginIndex = index;
					index++;
					if (index < length && str.charAt(index) == '|') {
						endIndex = index + 1;
						word = new Word();
						wordCount++;
						word.id = wordCount;
						word.line = line;
						word.value = str.substring(beginIndex, endIndex);
						word.type = Word.OPERATOR;
					} else {
						// endIndex=index;
						word = new Word();
						wordCount++;
						word.id = wordCount;
						word.line = line;
						word.value = str.substring(index - 1, index);
						word.type = Word.OPERATOR;
						index--;
					}
				} else if (temp == '+') {
					beginIndex = index;
					index++;
					if (index < length && str.charAt(index) == '+') {
						endIndex = index + 1;
						word = new Word();
						wordCount++;
						word.id = wordCount;
						word.line = line;
						word.value = str.substring(beginIndex, endIndex);
						word.type = Word.OPERATOR;

					} else {
						// endIndex=index;
						word = new Word();
						wordCount++;
						word.id = wordCount;
						word.line = line;
						word.value = str.substring(index - 1, index);
						word.type = Word.OPERATOR;
						index--;
					}
				} else if (temp == '-') {
					beginIndex = index;
					index++;
					if (index < length && str.charAt(index) == '-') {
						endIndex = index + 1;
						word = new Word();
						wordCount++;
						word.id = wordCount;
						word.line = line;
						word.value = str.substring(beginIndex, endIndex);
						word.type = Word.OPERATOR;
					} else { 
						// endIndex=index;
						word = new Word();
						wordCount++;
						word.id = wordCount;
						word.line = line;
						word.value = str.substring(index - 1, index);
						word.type = Word.OPERATOR;
						index--;
					}
				} else if (temp == '/') {
					index++;
					if (index < length && str.charAt(index) == '/')
						break;
					/*
					 * { index++; while(str.charAt(index)!='\n'){ index++; } }
					 */
					else if (index < length && str.charAt(index) == '*') {
						noteFlag = true;
					} else {
						word = new Word();
						wordCount++;
						word.id = wordCount;
						word.line = line;
						word.value = str.substring(index - 1, index);
						word.type = Word.OPERATOR;
					}
					index--;
				} 
				 else if (temp == '<') {
						beginIndex = index;
						index++;
						if (index < length && str.charAt(index) == '=') {
							endIndex = index + 1;
							word = new Word();
							wordCount++;
							word.id = wordCount;
							word.line = line;
							word.value = str.substring(beginIndex, endIndex);
							word.type = Word.OPERATOR;
						} else {
							// endIndex=index;
							word = new Word();
							wordCount++;
							word.id = wordCount;
							word.line = line;
							word.value = str.substring(index - 1, index);
							word.type = Word.OPERATOR;
							index--;
						}
					}
				 else if (temp == '>') {
						beginIndex = index;
						index++;
						if (index < length && str.charAt(index) == '=') {
							endIndex = index + 1;
							word = new Word();
							wordCount++;
							word.id = wordCount;
							word.line = line;
							word.value = str.substring(beginIndex, endIndex);
							word.type = Word.OPERATOR;
						} else {
							// endIndex=index;
							word = new Word();
							wordCount++;
							word.id = wordCount;
							word.line = line;
							word.value = str.substring(index - 1, index);
							word.type = Word.OPERATOR;
							index--;
						}
					}
				else {// 不是标识符、数字常量、字符串常量

					switch (temp) {
					case ' ':
					case '\t':
					case '\r':
					case '\n':
						word = null;
						break;// 过滤空白字符
					case ';':
						if(typestack.size()>0){
							typestack.pop();
						}
					case '[':
					case ']':
					case '(':
					case ')':
					case '{':
					case '}':
					case ',':
					case '"':
					case '.':
						// case '+':
						// case '-':
					case '*':
						// case '/':
					case '%':
					case '?':
					case '#':
						word = new Word();
						word.id = ++wordCount;
						word.line = line;
						word.value = String.valueOf(temp);
						if (Word.isOperator(word.value))
							word.type = Word.OPERATOR;
						else if (Word.isBoundarySign(word.value))
							word.type = Word.BOUNDARYSIGN;
						else
							word.type = Word.END;
						break;
					default:
						word = new Word();
						wordCount++;
						word.id = wordCount;
						word.line = line;
						word.value = String.valueOf(temp);
						word.type = Word.UNIDEF;
						word.flag = false;
						errorCount++;
						error = new Error(errorCount, "非法标识符", word.line, word);
						errorList.add(error);
						lexErrorFlag = true;
					}
				}
			} else {
				int i = str.indexOf("*/");
				if (i != -1) {
					noteFlag = false;
					index = i + 2;
					continue;
				} else
					break;
			}
			if (word == null) {
				index++;
				continue;
			}

			wordList.add(word);
			index++;
		}
	}

	public ArrayList<Word> lexAnalyse(String str) {
		String buffer[];
		if(str==null){
			if (!wordList.get(wordList.size() - 1).type.equals(Word.END)) {
				Word word = new Word(++wordCount, "#", Word.END, 1);
				wordList.add(word);
			}
			return wordList;
		}
		buffer = str.split("\n");
		int line = 1;
		for (int i = 0; i < buffer.length; i++) {
			analyse(buffer[i].trim(), line);
			line++;
		}
		if (!wordList.get(wordList.size() - 1).type.equals(Word.END)) {
			Word word = new Word(++wordCount, "#", Word.END, line++);
			wordList.add(word);
		}
		return wordList;
	}

//	public ArrayList<Word> lexAnalyse1(String filePath) throws IOException {
//		FileInputStream fis = new FileInputStream(filePath);
//		BufferedInputStream bis = new BufferedInputStream(fis);
//		InputStreamReader isr = new InputStreamReader(bis, "utf-8");
//		BufferedReader inbr = new BufferedReader(isr);
//		String str = "";
//		int line = 1;
//		while ((str = inbr.readLine()) != null) {
//			// System.out.println(str);
//			analyse(str.trim(), line);
//			line++;
//		}
//		inbr.close();
//		if (!wordList.get(wordList.size() - 1).type.equals(Word.END)) {
//			Word word = new Word(++wordCount, "#", Word.END, line++);
//			wordList.add(word);
//		}
//		return wordList;
//	}

	public String outputWordList() throws IOException {
		System.out.println(typelist);
		File file = new File("./result/");
		if (!file.exists()) {
			file.mkdirs();
			file.createNewFile();// 如果这个文件不存在就创建它
		}
		String path = file.getAbsolutePath();
		FileOutputStream fos = new FileOutputStream(path + "/wordList.txt");
		BufferedOutputStream bos = new BufferedOutputStream(fos);
		OutputStreamWriter osw1 = new OutputStreamWriter(bos, "utf-8");
		PrintWriter pw1 = new PrintWriter(osw1);
		pw1.println("单词序号\t单词的值\t单词类型\t单词所在行 \t单词是否合法\t单词属性");
		Word word;
		for (int i = 0; i < wordList.size(); i++) {
			word = wordList.get(i);
			pw1.println(word.id + "\t\t" + word.value + "\t\t" + word.type + "\t\t"
					+ "\t" + word.line + "\t" + word.flag+"\t\t\t"+word.attribute);
		}
		if (lexErrorFlag) {
			Error error;
			pw1.println("错误信息如下：");

			pw1.println("错误序号\t错误信息\t错误所在行 \t错误单词");
			for (int i = 0; i < errorList.size(); i++) {
				error = errorList.get(i);
				pw1.println(error.id + "\t\t" + error.info + "\t\t" + error.line
						+ "\t" + error.word.value);
			}
		} else {
			pw1.println("词法分析通过：");
		}
		pw1.close();
		return path + "/wordList.txt";
	}
	public static ArrayList<String> getTypelist(){
		ArrayList<String> list = new ArrayList<String>();
		
		for(Typeword x : typelist ){
			list.add(x.getValue());
		}
		System.out.println(list.toString());
		return list;
		
	}
//	public static ArrayList<String> getCharlist(){
//
//		return charlist;
//
//	}
	

	

//	public static void main(String[] args) throws IOException {
//		LexAnalyse lex = new LexAnalyse();
//		lex.lexAnalyse1("D:\\My Documents\\Workspace\\tinyCompiler\\b.c");
//		lex.outputWordList();
//	}

	public ArrayList<Word> getWordList() {
		// TODO Auto-generated method stub
		return wordList;
	}
}
