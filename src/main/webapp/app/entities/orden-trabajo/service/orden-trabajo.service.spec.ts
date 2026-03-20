import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { DATE_FORMAT } from 'app/config/input.constants';
import { IOrdenTrabajo } from '../orden-trabajo.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../orden-trabajo.test-samples';

import { OrdenTrabajoService, RestOrdenTrabajo } from './orden-trabajo.service';

const requireRestSample: RestOrdenTrabajo = {
  ...sampleWithRequiredData,
  fecha: sampleWithRequiredData.fecha?.format(DATE_FORMAT),
};

describe('OrdenTrabajo Service', () => {
  let service: OrdenTrabajoService;
  let httpMock: HttpTestingController;
  let expectedResult: IOrdenTrabajo | IOrdenTrabajo[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(OrdenTrabajoService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a OrdenTrabajo', () => {
      const ordenTrabajo = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(ordenTrabajo).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a OrdenTrabajo', () => {
      const ordenTrabajo = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(ordenTrabajo).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a OrdenTrabajo', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of OrdenTrabajo', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a OrdenTrabajo', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests.length).toBe(1);
    });

    describe('addOrdenTrabajoToCollectionIfMissing', () => {
      it('should add a OrdenTrabajo to an empty array', () => {
        const ordenTrabajo: IOrdenTrabajo = sampleWithRequiredData;
        expectedResult = service.addOrdenTrabajoToCollectionIfMissing([], ordenTrabajo);
        expect(expectedResult).toEqual([ordenTrabajo]);
      });

      it('should not add a OrdenTrabajo to an array that contains it', () => {
        const ordenTrabajo: IOrdenTrabajo = sampleWithRequiredData;
        const ordenTrabajoCollection: IOrdenTrabajo[] = [
          {
            ...ordenTrabajo,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addOrdenTrabajoToCollectionIfMissing(ordenTrabajoCollection, ordenTrabajo);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a OrdenTrabajo to an array that doesn't contain it", () => {
        const ordenTrabajo: IOrdenTrabajo = sampleWithRequiredData;
        const ordenTrabajoCollection: IOrdenTrabajo[] = [sampleWithPartialData];
        expectedResult = service.addOrdenTrabajoToCollectionIfMissing(ordenTrabajoCollection, ordenTrabajo);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(ordenTrabajo);
      });

      it('should add only unique OrdenTrabajo to an array', () => {
        const ordenTrabajoArray: IOrdenTrabajo[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const ordenTrabajoCollection: IOrdenTrabajo[] = [sampleWithRequiredData];
        expectedResult = service.addOrdenTrabajoToCollectionIfMissing(ordenTrabajoCollection, ...ordenTrabajoArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const ordenTrabajo: IOrdenTrabajo = sampleWithRequiredData;
        const ordenTrabajo2: IOrdenTrabajo = sampleWithPartialData;
        expectedResult = service.addOrdenTrabajoToCollectionIfMissing([], ordenTrabajo, ordenTrabajo2);
        expect(expectedResult).toEqual([ordenTrabajo, ordenTrabajo2]);
      });

      it('should accept null and undefined values', () => {
        const ordenTrabajo: IOrdenTrabajo = sampleWithRequiredData;
        expectedResult = service.addOrdenTrabajoToCollectionIfMissing([], null, ordenTrabajo, undefined);
        expect(expectedResult).toEqual([ordenTrabajo]);
      });

      it('should return initial array if no OrdenTrabajo is added', () => {
        const ordenTrabajoCollection: IOrdenTrabajo[] = [sampleWithRequiredData];
        expectedResult = service.addOrdenTrabajoToCollectionIfMissing(ordenTrabajoCollection, undefined, null);
        expect(expectedResult).toEqual(ordenTrabajoCollection);
      });
    });

    describe('compareOrdenTrabajo', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareOrdenTrabajo(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 14189 };
        const entity2 = null;

        const compareResult1 = service.compareOrdenTrabajo(entity1, entity2);
        const compareResult2 = service.compareOrdenTrabajo(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 14189 };
        const entity2 = { id: 22981 };

        const compareResult1 = service.compareOrdenTrabajo(entity1, entity2);
        const compareResult2 = service.compareOrdenTrabajo(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 14189 };
        const entity2 = { id: 14189 };

        const compareResult1 = service.compareOrdenTrabajo(entity1, entity2);
        const compareResult2 = service.compareOrdenTrabajo(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
