var game = [
    [-1, -1, -1, -1, -1, -1, -1, -1, -1, -1],
    [-1, -1,  1,  0,  0,  0,  0,  0, -1, -1],
    [-1, -1, -1, -1, -1, -1, -1,  0, -1, -1],
    [-1, -1,  2, -1, -1, -1, -1,  0, -1, -1],
    [-1, -1,  0, -1, -1, -1, -1,  0, -1, -1],
    [-1, -1,  0, -1, -1, -1, -1,  0, -1, -1],
    [-1, -1,  0,  0,  0,  0,  0,  0, -1, -1],
    [-1, -1, -1, -1, -1, -1, -1, -1, -1, -1],
    [-1, -1, -1, -1, -1, -1, -1, -1, -1, -1],
    [-1, -1, -1, -1, -1, -1, -1, -1, -1, -1]
];
var tp = new Tadpole();
tp.init(game, 3);