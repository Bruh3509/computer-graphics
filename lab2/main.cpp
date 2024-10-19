#include <string>
#include <opencv2/highgui/highgui.hpp>
#include <opencv2/imgproc.hpp>
#include <stdlib.h>
#include <dirent.h>
#include <sys/types.h>

cv::Mat medianFilter(const cv::Mat&, int);

cv::Mat dilatation(const cv::Mat&, const cv::Mat&);

cv::Mat otsu(const cv::Mat&, double);

cv::Mat simple(const cv::Mat&, double); 

char* findImage(char*);

int main(int argc, char** argv) {
	// input
	char* imagePath = "/home/bruh/Pictures/FractalFlame";

	char* res = findImage(imagePath);
	cv::Mat img = cv::imread(res, cv::IMREAD_COLOR);

	// filtering
	int kernel = 3;
	int kSize = 3;
	double thresh = 127;
	if (argc == 2)
	   	kernel = atoi(argv[1]);
	else if (argc == 3) {
		kernel = atoi(argv[1]);
		kSize = atoi(argv[2]);
	} else if (argc == 4) {
		kernel = atoi(argv[1]);
	   	kSize = atoi(argv[2]);
		thresh = atof(argv[3]);
	}	

	cv::Mat filteredImg = medianFilter(img, kernel);
	cv::Mat kernel1 = cv::getStructuringElement(cv::MORPH_RECT, cv::Size(kSize, kSize));
	cv::Mat dilateImg = dilatation(img, kernel1);
	cv::Mat otsuImg = otsu(img, thresh);
	cv::Mat simpleImg = simple(img, thresh);
	
	// saving images
	cv::imwrite("dilatation.png", dilateImg);
	cv::imwrite("median_blur.png", filteredImg);
	cv::imwrite("otsu.png", otsuImg);
	cv::imwrite("simple.png", simpleImg);
	cv::waitKey(0);

	// copy img
	printf("Original image: %s\n", res);
	char* copy = (char*) malloc(256 * sizeof(char));
	strcpy(copy, "cp ");
	strcat(copy, res);
	strcat(copy, " orig_img.png");
	system(copy);

	free(copy);
	free(res);
	return 0;
}

char* findImage(char* path) {
	DIR* dir = opendir(path);
	struct dirent** entries = (struct dirent**) malloc(256 * sizeof(struct dirent*));
	struct dirent* entry = readdir(dir);

	if (entry == NULL) exit(1);
	int i;
	for (i = 0; entry != NULL && i < 256; entry = readdir(dir)) {
		if (entry->d_type = DT_REG) entries[i++] = entry;
	}

	srand(time(0));
	int imgInd = i == 1 ? 0 : rand() % i;
	char* resPath = (char*) malloc(256 * sizeof(char));
	char* fileName = entries[imgInd]->d_name;

	strcpy(resPath, path);
	strcat(resPath, "/");
	strcat(resPath, fileName);
	
	free(entries);
	closedir(dir);
	return resPath;
}

cv::Mat simple(const cv::Mat& inputImg, double thresh) {
	cv::Mat outputImg, grayImg;
	cv::cvtColor(inputImg, grayImg, cv::COLOR_BGR2GRAY);

	cv::threshold(grayImg, outputImg, thresh, 255, cv::THRESH_BINARY);

	return outputImg;
}
cv::Mat otsu(const cv::Mat& inputImg, double thresh) {
	cv::Mat outputImg, grayImg;
	cv::cvtColor(inputImg, grayImg, cv::COLOR_BGR2GRAY);

	cv::threshold(grayImg, outputImg, thresh, 255, cv::THRESH_OTSU);

	return outputImg;
}

cv::Mat dilatation(const cv::Mat& inputImg, const cv::Mat& kernel) {
	cv::Mat outputImg;
	cv::dilate(inputImg, outputImg, kernel);
	
	return outputImg;
}

cv::Mat medianFilter(const cv::Mat& inputImg, int kernel) {
	cv::Mat outputImg;
	cv::medianBlur(inputImg, outputImg, kernel);

	return outputImg;
}

