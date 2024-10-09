#include <string>
#include <opencv2/highgui/highgui.hpp>

using namespace cv;

int main(int argc, char** argv) {
	std::string imagePath = "/home/bruh/Pictures/har.png";
	Mat img = imread(imagePath, IMREAD_COLOR);

	imshow("Test", img);
	waitKey(0);

	return 0;
}
