#!/usr/bin/env bash
FILES=(                                              \
  sections/{introduction,logic,project,how}.md       \
  references.md)

echo "compile.."
pandoc                               \
  --toc -N                           \
  --from         markdown            \
  --to           latex               \
  --template     template.tex        \
  --out          build/REPORT.pdf    \
  --latex-engine xelatex             \
  "${FILES[@]}"

echo "move files.."
cp build/REPORT.pdf ..

echo "done."
