package lox;

class AstPrinter implements Expr.Visitor<String>
{
	String print(Expr expr)
	{
		return expr.accept(this);
	}	// print

	@Override
	public String visitBinaryExpr(Expr.Binary expr)
	{
		return parenthesize(expr.operator.lexeme,
							expr.left, expr.right);
	}	// visitBinaryExpr

	@Override
	public String visitGroupingExpr(Expr.Grouping expr)
	{
		return parenthesize("group", expr.expression);
	}	// visitGroupingExpr

	@Override
	public String visitLiteralExpr(Expr.Literal expr)
	{
		if (expr.value == null) return "nil";
		return expr.value.toString();
	}	// visitLiteralExpr

	@Override
	public String visitUnaryExpr(Expr.Unary expr)
	{
		return parenthesize(expr.operator.lexeme, expr.right);
	}	// visitUnaryExpr

	private String parenthesize(String name, Expr... exprs)
	{
		StringBuilder builder = new StringBuilder();

		builder.append("(").append(name);
		for (Expr expr : exprs)
		{
			builder.append(" ");
			builder.append(expr.accept(this));
		}	// for
		builder.append(")");

		return builder.toString();
	}	// parenthesize
}	// AstPrinter