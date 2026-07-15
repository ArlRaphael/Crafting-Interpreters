package tool;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

public class GenerateAst
{
	public static void main (String[] args) throws IOException
	{
		if (args.length != 1)
		{
			System.err.println("Usage: generate_ast <output directory>");
			System.exit(64);
		}	// if
		String outputDir = args[0];
		defineAst(outputDir, "Expr", Arrays.asList(
			"Binary		: Expr left, Token operator, Expr right",
			"Grouping	: Expr expression",
			"Literal	: Object value",
			"Unary 		: Token operator, Expr right"
		));
	}	// main

	private static void defineAst(
		String outputDir, String baseName,
		List<String> types) throws IOException
	{
		String path = outputDir + "/" + baseName + ".java";
		PrintWriter writer = new PrintWriter(path, "UTF-8");

		writer.println("package lox;");
		writer.println();
		writer.println("import java.util.List;");
		writer.println();
		writer.println("abstract class " + baseName);
		writer.println("{");

		defineVisitor(writer, baseName, types);

		for (String type : types)
		{
			String className = type.split(":")[0].trim();
			String fields = type.split(":")[1].trim();
			defineType(writer, baseName, className, fields);
		}	// if

		// The base accept() method.
		writer.println();
		writer.println("	abstract <R> R accept(Visitor<R> visitor);");

		writer.println("}");
		writer.close();
	}	// defineAst

	private static void defineVisitor(
		PrintWriter writer, String baseName, List<String> types)
	{
		// one tab
		writer.println("	interface Visitor<R>");
		writer.println("	{");

		for (String type : types)
		{
			String typeName = type.split(":")[0].trim();
			// two tabs
			writer.println("		R visit" + typeName + baseName + "(" +
							typeName + " " + baseName.toLowerCase() + ");");
		}	// for

		writer.println("	}"); // one tab
		writer.println();
	}	// defineVisitor

	private static void defineType(
		PrintWriter writer, String baseName,
		String className, String fieldList)
	{
		// one tab
		writer.println("    static class " + className +
					   " extends " + baseName);
		writer.println("	{");

		// Constructor.
		writer.println("	    " + className + "(" + fieldList + ")");
		writer.println("		{"); // two tabs

		// Store parameters in fields.
		String[] fields = fieldList.split(", ");
		for (String field : fields)
		{
			String name = field.split(" ")[1];
			// three tabs
			writer.println("			this." + name + " = " + name + ";");
		}	// for

		writer.println("		}"); // two tabs

		// Visitor pattern.
		writer.println();
		// two tabs
		writer.println("		@Override");
		writer.println("		<R> R accept(Visitor<R> visitor)");
		writer.println("		{");
		// three tabs
		writer.println("			return visitor.visit" +
						className + baseName + "(this);");
		writer.println("		}");

		// Fields.
		writer.println();
		for (String field : fields)
		{
			writer.println("		final " + field + ";");
		}	// if

		writer.println("	}");
		writer.println();
	}	// defineType

}	// GenerateAst