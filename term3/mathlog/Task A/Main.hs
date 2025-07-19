module Main where

import Data.List (intercalate)

data Binop = Impl | Or | And
  deriving (Eq)

instance Show Binop where
  show Impl = "->"
  show Or   = "|"
  show And  = "&"

data Expr = Binary Binop Expr Expr
          | Not Expr
          | Var String
  deriving (Eq)

instance Show Expr where
  show (Binary op a b) = "(" ++ show op ++ "," ++ show a ++ "," ++ show b ++ ")"
  show (Not e)         = "(!" ++ show e ++ ")"
  show (Var name)      = name

data Token = AndL
           | OrL
           | ImplicationL
           | NotL
           | LeftBracketL
           | RightBracketL
           | VarL String
           deriving (Show, Eq)

alexScanTokens :: String -> [Token]
alexScanTokens [] = []
alexScanTokens (' ' : xs) = alexScanTokens xs
alexScanTokens ('\t' : xs) = alexScanTokens xs
alexScanTokens ('\n' : xs) = alexScanTokens xs
alexScanTokens ('(' : xs) = LeftBracketL : alexScanTokens xs
alexScanTokens (')' : xs) = RightBracketL : alexScanTokens xs
alexScanTokens ('|' : xs) = OrL : alexScanTokens xs
alexScanTokens ('&' : xs) = AndL : alexScanTokens xs
alexScanTokens ('!' : xs) = NotL : alexScanTokens xs
alexScanTokens ('-' : '>' : xs) = ImplicationL : alexScanTokens xs
alexScanTokens (x : xs)
  | x `elem` ['A'..'Z'] = let (var, rest) = span (`elem` (['A'..'Z'] ++ ['0'..'9'] ++ ['\''])) xs in
                            VarL (x : var) : alexScanTokens rest
  | otherwise = error $ "Unknown character: " ++ [x]

parse :: [Token] -> Expr
parse tokens = case parseImpl tokens of
  Just (expr, []) -> expr
  Just (_, rest)  -> error $ "Unexpected tokens after parsing: " ++ show rest
  Nothing          -> error "Invalid expression"

parseImpl :: [Token] -> Maybe (Expr, [Token])
parseImpl tokens = do
  (left, rest) <- parseDisj tokens
  parseImpl' left rest
  where
    parseImpl' left (ImplicationL : rest) = do
      (right, rest') <- parseImpl rest
      return (Binary Impl left right, rest')
    parseImpl' left rest = Just (left, rest)

parseDisj :: [Token] -> Maybe (Expr, [Token])
parseDisj tokens = do
  (left, rest) <- parseConj tokens
  parseDisj' left rest
  where
    parseDisj' left (OrL : rest) = do
      (right, rest') <- parseConj rest
      parseDisj' (Binary Or left right) rest'
    parseDisj' left rest = Just (left, rest)

parseConj :: [Token] -> Maybe (Expr, [Token])
parseConj tokens = do
  (left, rest) <- parseNeg tokens
  parseConj' left rest
  where
    parseConj' left (AndL : rest) = do
      (right, rest') <- parseNeg rest
      parseConj' (Binary And left right) rest'
    parseConj' left rest = Just (left, rest)

parseNeg :: [Token] -> Maybe (Expr, [Token])
parseNeg tokens = case tokens of
  (NotL : rest) -> do
    (expr, rest') <- parseNeg rest  
    return (Not expr, rest')  
  _ -> parseTerm tokens  

parseTerm :: [Token] -> Maybe (Expr, [Token])
parseTerm (VarL name : tokens) = Just (Var name, tokens)
parseTerm (LeftBracketL : tokens) = do
  (expr, rest) <- parseImpl tokens
  case rest of
    (RightBracketL : rest') -> Just (expr, rest')
    _ -> Nothing
parseTerm _ = Nothing

main :: IO ()
main = do
  input <- getLine
  let tokens = alexScanTokens input
  putStrLn $ show $ parse tokens
