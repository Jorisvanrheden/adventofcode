#include <iostream>

/* Assignment includes */
#include "../1/Assignment_1.h"
#include "../2/Assignment_2.h"
#include "../3/Assignment_3.h"
#include "../4/Assignment_4.h"
#include "../5/Assignment_5.h"
#include "../6/Assignment_6.h"
#include "../7/Assignment_7.h"
#include "../8/Assignment_8.h"
#include "../9/Assignment_9.h"
#include "../10/Assignment_10.h"
#include "../11/Assignment_11.h"
#include "../12/Assignment_12.h"
#include "../13/Assignment_13.h"
#include "../14/Assignment_14.h"
#include "../15/Assignment_15.h"
#include "../16/Assignment_16.h"
#include "../17/Assignment_17.h"
#include "../18/Assignment_18.h"
#include "../19/Assignment_19.h"
#include "../20/Assignment_20.h"
#include "../21/Assignment_21.h"
#include "../22/Assignment_22.h"
#include "../23/Assignment_23.h"

int main()
{
    Assignment_23 assignment;

    std::vector<std::string> input = Utilities::readFile(assignment.getInput());

    assignment.initialize(input);
    assignment.run();
}
