# TreeColouring
This is an implementation of ancestral colouring for imperfect Merkle trees.

The program takes two arguments:
+ The number of transactions of the tree
+ A feasible colour sequence for the tree

Besides, the colourSplitting() is used to colour the tree, whereas the validateTreeColouring() can be used to verify if the tree is coloured correctly.

Users can also modify the code to run the getFeasibleSequenceList() for generating feasible colour sequences for a particular Merkle tree.